$(document).ready(function() {
  /* 
   * 
   * Landing Page 
   *
   * 
  */

  // When the user clicks a hero button
$(".btn-hero").click(function() {
  $(".general-fact").delay(100).fadeOut(400);
  $(".overlay").delay(750).fadeOut(400).hide("slow", function() {
    $(".hero-image-container:first-of-type").append("<div class='hero-image-overlay'></div>").css("position", "relative").append("<div class='hero-image-title'><h3>Young</h3></div>");
    $(".hero-image-overlay").fadeIn(100); 
    $(".hero-image-container:nth-of-type(2)").append("<div class='hero-image-overlay'></div>").css("position", "relative").append("<div class='hero-image-title'><h3>Female</h3></div>");
    $(".hero-image-overlay").fadeIn(100); 
    $(".hero-image-container:nth-of-type(3)").append("<div class='hero-image-overlay'></div>").css("position", "relative").append("<div class='hero-image-title'><h3>Elderly</h3></div>");
    $(".hero-image-overlay").fadeIn(100); 
  });
});


// When the user hovers over each hero image
$(".hero-image-container").hover(function() {
  $(this).find(".hero-image-overlay").addClass("overlay1");
  $(this).find(".hero-image-title").addClass("hero-image-title-hover").append("<i class='fas fa-info-circle'></i>");
  
  
}, function() {
  $(this).find(".hero-image-overlay").removeClass("overlay1");
  $(this).find(".hero-image-title").removeClass("hero-image-title-hover");
  $(".fas.fa-info-circle").remove();
});

  




// When the user clicks one of the hero images 
$(".hero-image-container").click(function() {

  var thisHeroImageCotainer = $(this).closest(".hero-image-container");
  // nullify hover
  thisHeroImageCotainer.toggleClass("hero-image-container").toggleClass("hero-image-container-clicked");
  // Depending on which container the user clicks, jQuery will add an appropriate class. For example,
  // if the user clicks the fist conatiner, add a "first" class to the element. 
  thisHeroImageCotainer.find(".hero-image").toggleClass("hero-image-clicked");
  $(".hero-image-container").not(thisHeroImageCotainer).fadeOut(500).hide();
  thisHeroImageCotainer.parent().toggleClass("hero-image-grid-container-clicked");
  thisHeroImageCotainer.find(".hero-image-overlay").fadeOut(500).hide();
  thisHeroImageCotainer.find(".hero-image-title").fadeOut(500).hide();
  thisHeroImageCotainer.find(".hero-text-container").toggleClass("hero-text-container-clicked");
  thisHeroImageCotainer.parent().toggleClass("grey-background");

});

$(".fas.fa-undo-alt").click(function(event) {
  event.stopPropagation();
  event.stopImmediatePropagation()
  var thisHeroImageContainer = $(this).closest(".hero-image-container-clicked");
  thisHeroImageContainer.toggleClass("hero-image-container").toggleClass("hero-image-container-clicked");
  thisHeroImageContainer.find(".hero-image").toggleClass("hero-image-clicked");
  $(".hero-image-container").not(thisHeroImageContainer).fadeIn(500);
  thisHeroImageContainer.parent().toggleClass("hero-image-grid-container-clicked");
  thisHeroImageContainer.find(".hero-image-overlay").fadeIn(500);
  thisHeroImageContainer.find(".hero-image-title").fadeIn(500);
  thisHeroImageContainer.find(".hero-text-container").toggleClass("hero-text-container-clicked");
  thisHeroImageContainer.parent().toggleClass("grey-background");

  
  // removeClass().addClass("hero-image-container");

});



// Logo animation
$(".dot").addClass("changed");




  /* 
   * 
   * Homescape
   *
   * 
  */








  
  // Range Slider & Make ajax calls
  var mySlider = new rSlider({
    target: '#glanceSlider',
    values: [0, 10, 20, 30, 40, 50, 60, "60+"], // + to be added
    range: true,
    tooltip: true,
    scale: true,
    set: [0, "60+"],
    width: '300px',
    onChange: function (vals) {
      var value = mySlider.getValue();
      var commaPosition = value.indexOf(",");
      var startAge = value.substr(0, commaPosition);
      var endAge = value.substr(commaPosition + 1, value.length);

      if (startAge === endAge) {
        mySlider.setValues(0, "60+");
      }

      ajaxCallPopulation();
      ajaxCallLgas();
      
    }
  });

  

  // When there's change in any input box, send status information of every input box. 
  $(".input-group").change(function() {
    ajaxCallPopulation();

  });

  // When sortby changes
  $("#sort-by").change(function() {
    ajaxCallPopulation();
  });

  function ajaxCallPopulation() {
    var groupCBs = []; 
    var groupIsChecked = []; 
    var sliderVals = "";
    var stateVals = {};
    var lgaVals = {};
    var sortbyVal = $("#sort-by").val();
    
    $(".group-filter-container input:radio").each(function() {
      groupCBs.push($(this).attr('value'));
      groupIsChecked.push($(this).is(":checked"));
    });

    sliderVals = mySlider.getValue();
    console.log("age is " + sliderVals);

    $(".state-filter-container input:checkbox").each(function() {
      if ($(this).is(":checked")) {
        var value = $(this).attr('value');
        var isChecked = $(this).is(":checked");
        stateVals[value] = isChecked;
      }
    });

    // If states are selected and lga-filter-container is generated
    if ($(".lga-filter-container input:checkbox").length) {
      $(".lga-filter-container input:checkbox").each(function() {
        if ($(this).is(":checked")) {
          var value = $(this).attr('value');
          var isChecked = $(this).is(":checked");
          lgaVals[value] = isChecked;
          
        }
      });    
    } 

    $.ajax({
      url: '/ProcessFilter',
      type: 'POST',
      data: {
        isCheckedBothGroup: groupIsChecked[0],
        cbBothGroup: groupCBs[0],
        isCheckedHomeless: groupIsChecked[1],
        cbHomeless: groupCBs[1],
        isCheckedAtRisk: groupIsChecked[2],
        cbAtRisk: groupCBs[2],
        isCheckedBothGender: groupIsChecked[3],
        cbBothGender: groupCBs[3],
        isCheckedMale: groupIsChecked[4],
        cbMale: groupCBs[4],
        isCheckedFemale: groupIsChecked[5],
        cbFemale: groupCBs[5],
        ageRange: sliderVals,
        stateVals: JSON.stringify(stateVals),
        lgaVals: JSON.stringify(lgaVals),
        sortbyVal: sortbyVal
      },
      success: function (response) {
        $("#ajax-container").html(response);
         // Apply DataTables
        var populationTable = $('#table_id').DataTable({
          "dom": 'ftipr',
          "pageLength": 5
        });



        var chartLabels = populationTable.column(1).data();
        var chartData = populationTable.column(6).data();
        let result = chartData.map(i => Number(i));
        var backgroundColor = [];
        var borderColor = [];
        
        for (var i = 0; i < chartLabels.length; i++) {
          console.log(typeof 3);
        }

        for (var i  = 0; i < chartLabels.length; i++) {
          var color1 = Math.floor(Math.random() * 255);
          var color2 = Math.floor(Math.random() * 255);
          var color3 = Math.floor(Math.random() * 255);
          backgroundColor.push("rgba(" + color1 + ", " + color2  + ", " + color3 + ", 0.2)");
          borderColor.push("rgba(" + color1 + ", " + color2  + ", " + color3 + ", 1)");
        }

        console.log(" " + typeof chartData + " " + typeof chartLabels + " " + typeof backgroundColor + " " + typeof borderColor);
        

        

        var testLabels = ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'];
        var testData = [12, 19, 3, 5, 2, 3];
        var testBackgroundColor = [                          'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(255, 159, 64, 0.2)'];
        var testBorderColor = [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64, 1)'
        ];

        console.log(" " + typeof testData + " " + typeof testLabels + " " + typeof testBackgroundColor + " " + typeof testBorderColor);
          
          var ctx = document.getElementById('myChart').getContext('2d');
          var data = {
            datasets: [{
              data: chartData,
              backgroundColor: backgroundColor,
            }],
            labels: chartLabels
          };

          var myChart = new Chart(ctx, {
              type: 'pie',
              data: data,
              options: {
                maintainAspectRatio: false,
              legend: {
                position: 'bottom',
              labels: {
                boxWidth: 12
              }
            }
            }
              
          });

          // var chartLabels = populationTable.column(1).data();
          // var chartData = populationTable.column(6).data();
  
          // for (var i = 0; i < chartData.length; i++) {
          //   console.log(typeof chartData[i]);
          // }
  
          // 
          
  
  
          //   var ctx = document.getElementById('myChart').getContext('2d');
          //   var myChart = new Chart(ctx, {
          //       type: 'pie',
          //       data: {
          //           labels: chartLabels,
          //           datasets: [{
          //               label: '# of Populations',
          //               data: chartData
                        
          //           }]
          //       }
          //   });


        
      }, 
      error: function (response) {
        alert("Failed:" + response);
      }
    });
  }





  // Location Popups for States
  $("#loc-states").click(function() {
    $(".pop-up-overlay").toggleClass("display-initial");
    $("#loc-states-pop-up").toggleClass("display-flex");
    if ($.fn.dataTable.isDataTable("#table-states")) {
      $("#table-states").DataTable();
    } else {
      $("#table-states").DataTable({
        scrollY: 300,
        scroller: true,
        "paging": false,
        "info": false,
        "caseInsensitive": false
      });
    }
    $('.th-select').removeClass();
  });

  // Select all states 
  $(document).on('click', '#selectNoStates', function() {
    $('#loc-states-pop-up input:checkbox').each(function() {
      $(this).prop('checked', false);
    });
  });
  // Deselect all states
  $(document).on('click', '#selectAllStates', function() {
    $('#loc-states-pop-up input:checkbox').each(function() {
      $(this).prop('checked', true);
    });
  });




  // Location Popups for LGAs
  $("#loc-lgas").click(function() {
    $("#loc-lgas-pop-up").toggleClass("display-flex");    
    $(".pop-up-overlay").toggleClass("display-initial");
  });

  $(document).on('click', "#states-ok-button", function() {
    var stateVals = [];
    $(".state-filter-container input:checkbox").each(function() {
      if ($(this).is(":checked")) {
        var value = $(this).attr('value');
        stateVals.push(value);
    }
    
    // Change the content of the button
    var numbOfStatesSelected = stateVals.length;
    var updatedHtml = "States";
    if (numbOfStatesSelected == 9) {
      updatedHtml += "--All--";
    } else {
      for (var i = 0; i < numbOfStatesSelected; i++) {
      
        if (i == 0) {
          updatedHtml += ": " + stateVals[i];
        } else {
          updatedHtml += ", " + stateVals[i];
        }
        
      }
    }
    $("#loc-states").html(updatedHtml);
    $('.th-select').removeClass();
  });
    $('#loc-lgas-pop-up input:checkbox').each(function() {
      $(this).prop('checked', false);
    });
    ajaxCallPopulation();
    ajaxCallLgas();
    $('#loc-states-pop-up').toggleClass("display-flex");
    $(".pop-up-overlay").toggleClass("display-initial");
    
  });

  $(document).on('click', "#states-cancel-button", function() {
    $('#loc-states-pop-up').toggleClass("display-flex");
    $(".pop-up-overlay").toggleClass("display-initial");
  });

  // ajaxCallPopulation upon clicking #lga-ok-button
  $(document.body).on('click', '#lga-ok-button', function() {
    $('#loc-lgas-pop-up').toggleClass("display-flex");
    $(".pop-up-overlay").toggleClass("display-initial");
    ajaxCallPopulation();
  });

  $(document).on('click', "#lga-cancel-button", function() {
    $('#loc-lgas-pop-up').toggleClass("display-flex");
    $(".pop-up-overlay").toggleClass("display-initial");
  });

  function ajaxCallLgas() {
    var cbs = [];
    var isChecked = [];

    $('#loc-states-pop-up input:checkbox').each(function() {
      cbs.push($(this).attr('value'));
      isChecked.push($(this).is(":checked"));
    });
    
    $.ajax({
      url: '/ProcessLGAFilter',
      type: 'POST',
      data: {
        isCheckedNSW: isChecked[0],
        cbNSW: cbs[0],
        isCheckedVIC: isChecked[1],
        cbVIC: cbs[1],
        isCheckedQLD: isChecked[2],
        cbQLD: cbs[2],
        isCheckedSA: isChecked[3],
        cbSA: cbs[3],
        isCheckedWA: isChecked[4],
        cbWA: cbs[4],
        isCheckedTAS: isChecked[5],
        cbTAS: cbs[5],
        isCheckedNT: isChecked[6],
        cbNT: cbs[6],
        isCheckedACT: isChecked[7],
        cbACT: cbs[7],
        isCheckedOTHER: isChecked[8],
        cbOTHER: cbs[8]
      },
      success: function (response) {
        $("#loc-lgas-pop-up").html(response);
        
        $("#table-lgas").DataTable({
          scrollY: 300,
          scroller: true,
          "paging": false,
          "info": false,
          "caseInsensitive": false
        });
      }, 
      error: function (response) {
        alert("Failed:" + response);
      }
    });
  }

  // Process LGAs
  $(document.body).on('click', '#states-ok-button', function() {
    ajaxCallPopulation();
    ajaxCallLgas();
    

  });


  $('#reset-query').click(function() {
    location.reload();
  });
 

 


}); 



/*
 *
 *
 * Dive 
 *
 * 
 */

(function() {
  var diveSlider = new rSlider({
    target: '#diveSlider',
    values: [0, 10, 20, 30, 40, 50, 60, "60+"], // + to be added
    range: true,
    tooltip: true,
    scale: true,
    set: [0, "60+"],
    width: '300px',
    onChange: function (vals) {
      var value = mySlider.getValue();
      var commaPosition = value.indexOf(",");
      var startAge = value.substr(0, commaPosition);
      var endAge = value.substr(commaPosition + 1, value.length);

      if (startAge === endAge) {
        mySlider.setValues(0, "60+");
      }

      // ajaxCallPopulation();
      // ajaxCallLgas();
      
    }
  });
})();


