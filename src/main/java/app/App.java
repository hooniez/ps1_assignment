package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;

/**
 * Main Application Class.
 * <p>
 * Running this class as regular java application will start the
 * Javalin HTTP Server and our web application.
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class App {

    public static final int JAVALIN_PORT = 4000;
    public static final String CSS_DIR = "css/";
    public static final String IMAGES_DIR = "images/";
    public static final String JS_DIR = "js/";

    public static void main(String[] args) {
        // Create our HTTP server and listen in port 7000
        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new RouteOverviewPlugin("/help/routes"));

            // Uncomment this if you have files in the CSS Directory
            config.addStaticFiles(CSS_DIR);

            // Uncomment this if you have files in the Images Directory
            config.addStaticFiles(IMAGES_DIR);

            config.addStaticFiles(JS_DIR);
        }).start(JAVALIN_PORT);

        // Configure Web Routes
        configureRoutes(app);
    }

    public static void configureRoutes(Javalin app) {
        // All webpages are listed here as GET pages
        app.get(Index.URL, new Index());
        app.get(Glance.URL, new Glance());
        app.get(Dive.URL, new Dive());

        // Add / uncomment POST commands for any pages that need web form POSTS

        app.post("/ProcessFilter", ctx -> {

            String html = "";
            ArrayList<String> isCheckedStrings = new ArrayList<String>();
            ArrayList<Boolean> isChecked = new ArrayList<Boolean>();
            ArrayList<String> states = new ArrayList<String>();
            ArrayList<Boolean> statesChecked = new ArrayList<Boolean>();
            ArrayList<String> lgas = new ArrayList<String>();
            ArrayList<Boolean> lgasChecked = new ArrayList<Boolean>();
            String sortbyVal = ctx.formParam("sortbyVal");

            // States: Turn a JSON string into ArrayLists
            String stateValsJson = ctx.formParam("stateVals");
            ObjectMapper stateValsObjectMapper = new ObjectMapper();
            JsonNode stateValsJsonNode = stateValsObjectMapper.readTree(stateValsJson);
            Iterator<String> stateValsFieldNames = stateValsJsonNode.fieldNames();

            while (stateValsFieldNames.hasNext()) {
                String fieldName = stateValsFieldNames.next();

                // JsonNode field = jsonNode.get(fieldName);
                Boolean field = stateValsJsonNode.get(fieldName).asBoolean();

                states.add(fieldName);
                statesChecked.add(field);

                // System.out.println("each fieldName is " + fieldName);
                // System.out.println("each field is " + field);
            }

            // LGAs: Turn a JSON string into ArrayLists
            String lgaValsJson = ctx.formParam("lgaVals");
            ObjectMapper lgaValsObjectMapper = new ObjectMapper();
            JsonNode lgaValsJsonNode = lgaValsObjectMapper.readTree(lgaValsJson);
            Iterator<String> lgaValsFieldNames = lgaValsJsonNode.fieldNames();

            while (lgaValsFieldNames.hasNext()) {
                String fieldName = lgaValsFieldNames.next();

                // JsonNode field = jsonNode.get(fieldName);
                Boolean field = lgaValsJsonNode.get(fieldName).asBoolean();

                lgas.add(fieldName);
                lgasChecked.add(field);

                // System.out.println("each fieldName is " + fieldName);
                // System.out.println("each field is " + field);
            }

            // Parse ageRange
            String ageRange = ctx.formParam("ageRange");
            System.out.println(ageRange);
            // Examples of output are 0,60+ OR 0,40
            String startAgeString = ageRange.substring(0, ageRange.indexOf(","));
            String endAgeString = ageRange.substring(ageRange.indexOf(",") + 1);

            if (endAgeString.equals("60+")) {
                // convert 60+ to 70 for the purpose of parsing it
                endAgeString = "70";
            }

            Integer startAge = Integer.parseInt(startAgeString);
            Integer endAge = Integer.parseInt(endAgeString);

            System.out.println("Start age is " + startAge);
            System.out.println("End age is " + endAge);

            HashMap<Integer, Boolean> isInAgeRange = new HashMap<Integer, Boolean>();
            for (Integer age = 0; age <= 70; age += 10) {
                if (age < startAge || age > endAge) {
                    isInAgeRange.put(age, false);
                } else {
                    isInAgeRange.put(age, true);
                }
            }

            System.out.println("isInAgeRange " + isInAgeRange);

            isCheckedStrings.add(ctx.formParam("isCheckedBothGroup"));
            isCheckedStrings.add(ctx.formParam("isCheckedHomeless"));
            isCheckedStrings.add(ctx.formParam("isCheckedAtRisk"));
            isCheckedStrings.add(ctx.formParam("isCheckedBothGender"));
            isCheckedStrings.add(ctx.formParam("isCheckedMale"));
            isCheckedStrings.add(ctx.formParam("isCheckedFemale"));
            // isCheckedStrings.add(isCheckedAge);

            for (int i = 0; i < isCheckedStrings.size(); i++) {
                isChecked.add(Boolean.parseBoolean(isCheckedStrings.get(i)));
            }

            ArrayList<String> cbVals = new ArrayList<String>();

            cbVals.add(ctx.formParam("cbBothGroup"));
            cbVals.add(ctx.formParam("cbHomeless"));
            cbVals.add(ctx.formParam("cbAtRisk"));
            cbVals.add(ctx.formParam("cbBothGender"));
            cbVals.add(ctx.formParam("cbMale"));
            cbVals.add(ctx.formParam("cbFemale"));

            JDBCConnection jdbc = new JDBCConnection();
            ArrayList<String> populations = jdbc.getPopulation(isChecked, cbVals, isInAgeRange, states, statesChecked,
                    lgas, lgasChecked, sortbyVal);

            if (sortbyVal.equals("lgas")) {
                html += "<table id='table_id' class='display'>";
                html += "   <thead>";
                html += "       <tr>";
                html += "           <th>Year</th>";
                html += "           <th>LGA</th>";
                html += "           <th>State</th>";
                html += "           <th>Type</th>";
                html += "           <th>Gender</th>";
                html += "           <th>Age</th>";
                html += "           <th>Population</th>";
                html += "       </tr>";
                html += "   </thead>";
                html += "   <tbody>";
                for (int i = 0; i < populations.size(); i += 7) {
                    html += "<tr>";
                    html += "   <td>" + populations.get(i) + "</td>";
                    html += "   <td>" + populations.get(i + 1) + "</td>";
                    html += "   <td>" + populations.get(i + 2) + "</td>";
                    // Both homeless and at-risk
                    if (isChecked.get(0)) {
                        html += "   <td>Both</td>";
                    } else {
                        html += "   <td>" + populations.get(i + 3).substring(0, 1).toUpperCase()
                                + populations.get(i + 3).substring(1) + "</td>";
                    }
                    // Both male and female
                    if (isChecked.get(3)) {
                        html += "   <td>Both</td>";
                    } else {
                        html += "   <td>" + populations.get(i + 4).substring(0, 1).toUpperCase()
                                + populations.get(i + 4).substring(1) + "</td>";
                    }
                    if (endAge == 70) {
                        html += "   <td>" + startAge + "..." + "60+</td>";
                    } else {
                        html += "   <td>" + startAge + "..." + endAge + "</td>";
                    }

                    html += "   <td>" + populations.get(i + 6) + "</td>";
                    html += "</tr>";
                }
                html += "</tbody>";
                html += "</table>";
            } else if (sortbyVal.equals("states")) {
                html += "<table id='table_id' class='display'>";
                html += "   <thead>";
                html += "       <tr>";
                html += "           <th>Year</th>";
                html += "           <th>State</th>";
                html += "           <th>Type</th>";
                html += "           <th>Gender</th>";
                html += "           <th>Age</th>";
                html += "           <th>Population</th>";
                html += "       </tr>";
                html += "   </thead>";
                html += "   <tbody>";
                for (int i = 0; i < populations.size(); i += 6) {
                    html += "<tr>";
                    html += "   <td>" + populations.get(i) + "</td>";
                    html += "   <td>" + populations.get(i + 1) + "</td>";
                    // Both homeless and at-risk
                    if (isChecked.get(0)) {
                        html += "   <td>Both</td>";
                    } else {
                        html += "   <td>" + populations.get(i + 2).substring(0, 1).toUpperCase()
                                + populations.get(i + 2).substring(1) + "</td>";
                    }
                    // Both male and female
                    if (isChecked.get(3)) {
                        html += "   <td>Both</td>";
                    } else {
                        html += "   <td>" + populations.get(i + 3).substring(0, 1).toUpperCase()
                                + populations.get(i + 3).substring(1) + "</td>";
                    }
                    if (endAge == 70) {
                        html += "   <td>" + startAge + "..." + "60+</td>";
                    } else {
                        html += "   <td>" + startAge + "..." + endAge + "</td>";
                    }
                    html += "   <td>" + populations.get(i + 5) + "</td>";
                    html += "</tr>";
                }
                html += "</tbody>";
                html += "</table>";
            }

            ctx.result(html);

        });

        app.post("/ProcessLGAFilter", ctx -> {
            String html = "";
            ArrayList<String> isCheckedStrings = new ArrayList<String>();
            ArrayList<Boolean> isChecked = new ArrayList<Boolean>();

            isCheckedStrings.add(ctx.formParam("isCheckedNSW"));
            isCheckedStrings.add(ctx.formParam("isCheckedVIC"));
            isCheckedStrings.add(ctx.formParam("isCheckedQLD"));
            isCheckedStrings.add(ctx.formParam("isCheckedSA"));
            isCheckedStrings.add(ctx.formParam("isCheckedWA"));
            isCheckedStrings.add(ctx.formParam("isCheckedTAS"));
            isCheckedStrings.add(ctx.formParam("isCheckedACT"));
            isCheckedStrings.add(ctx.formParam("isCheckedOTHER"));

            for (int i = 0; i < isCheckedStrings.size(); i++) {
                isChecked.add(Boolean.parseBoolean(isCheckedStrings.get(i)));
            }

            ArrayList<String> cbVals = new ArrayList<String>();
            cbVals.add(ctx.formParam("cbNSW"));
            cbVals.add(ctx.formParam("cbVIC"));
            cbVals.add(ctx.formParam("cbQLD"));
            cbVals.add(ctx.formParam("cbSA"));
            cbVals.add(ctx.formParam("cbWA"));
            cbVals.add(ctx.formParam("cbTAS"));
            cbVals.add(ctx.formParam("cbACT"));
            cbVals.add(ctx.formParam("cbOTHER"));

            JDBCConnection jdbc = new JDBCConnection();
            ArrayList<String> lgas = jdbc.getLGAs(isChecked, cbVals);

            html += "<h3>Filter by LGAs</h3>";
            html += "<table id='table-lgas' class='display'>";
            html += "   <thead>";
            html += "       <tr>";
            html += "           <th class='th-select'>Select</th>";
            html += "           <th>LGA Name</th>";
            html += "           <th>State Name</th>";
            html += "       </tr>";
            html += "   </thead>";
            html += "   <tbody>";

            for (int i = 0; i < lgas.size(); i += 2) {
                String lgaName = lgas.get(i);
                String stateName = lgas.get(i + 1);

                html += "<tr>";
                html += "   <td>";
                html += "       <input type='checkbox' name='lgas' id='" + lgaName + "' value='" + lgaName + "'>";
                html += "   </td>";
                html += "   <td>";
                html += "       <label for='" + lgaName + "'>" + lgaName + "</label>";
                html += "   </td>";
                html += "   <td>";
                html += "       <span>" + stateName + "</span>";
                html += "   </td>";
                html += "</tr>";

            }

            html += "</tbody>";
            html += "</table>";

            html += "<div class='pop-up-button-conatiner'>";
            html += "   <button class='btn' id='lga-ok-button'>Ok</button>";
            html += "   <button class='btn' id='lga-cancel-button'>Cancel</button>";
            html += "</div>";

            ctx.result(html);

        });

    }

}
