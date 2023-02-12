# Studio Project  Starter Code
Starter code for the Studio Project. This is a simple layout connected to a GitHub Classroom that serves as the basis for implementing the studio project. 

This example program provides:

* A Java class for the Index page (index.html)
* 6x Java classes for 6 pages. Additional pages can be added by adding additional classes
* JDBCConnection Java class, that uses the Movies Database which is the consistent database example that is used in the project workshop code.
    * You will need to replace the Movies database with the database for the data set for your chosen social challenge
    * You will need to update the DATABASE string in JDBCConnection for the new database name
    * You should replace the example method in JDBCConnection with the required SQL Queries to support your web page
* App Java class to configure and setup the Javalin web server. 
    * You will need to uncomment the appropriate lines in ```configureRoutes()``` for any webpages that you need to power through a web form.
* Examples CSS (common.css) file in the resources directory
* Example image (logo.png) file in the resources directory with where to locate any images you want on your website

Classes backing Web pages:
```bash
├── Index                        - Homepages/index page. Provides a directory to all other pages
├── Page1                        - Sets of 6 other Java class files backing 6 other pages.
├── ...
└── Page6                        
```

Other Classes:
```bash
├── App                          - Main Application entrypoint for Javalin
└── JDBCConnection               - Example JDBC Connection class based on Studio Project Workshop content
```

Folders:
```bash
├── /src/main                    - Location of all files as required by MAVEN build
│         ├── java               - Java Source location
│         │    └── app           - package location for all Java files
│         └── resources          - Web resources (html templates / style sheets)
│               ├── css          - CSS Style-sheets. Base example style sheet (common.css) provided
│               └── images       - Image files. Base example image (RMIT Logo) provided
│ 
├── /target                      - build directory (DO NOT MODIFY)
├── /database                    - The folder to store sqlite database files (*.db files)
├── pom.xml                      - Configure Build (DO NOT MODIFY)
└── README.md                    - This file ;)
```

Current Libraries
* org.xerial.sqlite-jdbc         - SQLite JDBC library
* javalin (lightweight java webserver)

Libraries required as dependencies
* By javalin
   * slf4j-simple (lightweight logging)
* By xerial/jdbc
   * sqlite-jdbc

# Building & Running the code
1. Open this project within VSCode
2. Allow VSCode to read the Maven pom.xml file
 - Allow the popups to run and "say yes" to VSCode configuring Maven
 - Allow VSCode to download the required Java libraries
3. To Build & Run
 - Open the src/main/java/app/App.java source file, and select "Run" from the pop-up above the main function
4. Go to: http://localhost:7000

# Authors
* Dr. Timothy Wiley, School of Computing Technologies, STEM College, RMIT University.
* Prof. Santha Sumanasekara, School of Computing Technologies, STEM College, RMIT University.

Copyright RMIT University (c) 2021

