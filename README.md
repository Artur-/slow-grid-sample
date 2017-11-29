slow grid sample
================

A sample project to demonstrate the slowness of the grid being generated (rendered) in the browser using the basic Vaadin template with the latest Vaadin 8.1.6 release.

Vaadin Issue #10232 on GitHub: https://github.com/vaadin/framework/issues/10232 


Performance Results:
====================

- Run the application, run "mvn jetty:run" and open http://localhost:8080/ for manual tests.

or

- Run "mvn integration-tests" for automated tests using Testbench (CVAL license required)


Performance
===========

Automated test results run locally on 64 Bit on Windows 7 64 Bit OS

|Browser   |V8.1.6 Grid (Columns, Hidden Columns, Rows)|Rendering Time|Request Time|
|----------|-------------------------------------------|--------------|------------|
|CH|Grid (11, 1, 1000)|1167|17|
|CH|Grid (100, 0, 1000)|3066|42|
|CH|Grid (100, 0, 3000)|2925|33|
|CH|Grid (100, 20, 3000)|5775|38|
|FF|Grid (11, 1, 1000)|2693|7|
|FF|Grid (100, 0, 1000)|8948|25|
|FF|Grid (100, 0, 3000)|8733|68|
|FF|Grid (100, 20, 3000)|12165|44|
|IE|Grid (11, 1, 1000)|2887|7|
|IE|Grid (100, 0, 1000)|10598|30|
|IE|Grid (100, 0, 3000)|10622|24|

Grid with 80 columns and 1000 rows:
-----------------------------------
Manually tested on local browser with 64 Bit on Windows 7 64 Bit OS - no slow test VMs.

|Browser   |V8.1.6 Grid|
|----------|-----------|
|Chrome    |~ 3 seconds|
|Firefox 55|>12s       |
|IE11      |~ 6 seconds|

Additional test notes: Firefox reported an error because the script was still running.


Project Usage
=============

To compile the entire project, run "mvn install" using Maven > 3.

To run the application, run "mvn jetty:run" and open http://localhost:8080/ 
- then change the columns, hidden columns and rows to your liking and generate the grid after hiding it

To produce a deployable production mode WAR:
- change productionMode to true in the servlet class configuration (nested in the UI class)
- run "mvn clean package"
- test the war file with "mvn jetty:run-war"
