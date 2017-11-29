slow grid sample
================

A sample project to demonstrate the slowness of the grid being generated (rendered) in the browser using the basic Vaadin template with the latest Vaadin 8.1.6 release.

Vaadin Issue #10232 on GitHub: https://github.com/vaadin/framework/issues/10232 


Performance Results:
====================

1. Run the application, run "mvn jetty:run" and open http://localhost:8080/


Performance
===========

Grid with 80 columns and 1000 rows:
-----------------------------------
Tested on local browser with 64 Bit on Windows 7 64 Bit OS - no slow test VMs.

||Browser||V8.1.6 Grid||
|Chrome|~ 3 seconds|
|Firefox 55|>12s|
|IE11|~ 6 seconds|

Additional test notes: Firefox reported a script still running warning error.


Project Usage
=============

To compile the entire project, run "mvn install" using Maven > 3.

To run the application, run "mvn jetty:run" and open http://localhost:8080/ 
- then change the columns, hidden columns and rows to your liking and generate the grid after hiding it

To produce a deployable production mode WAR:
- change productionMode to true in the servlet class configuration (nested in the UI class)
- run "mvn clean package"
- test the war file with "mvn jetty:run-war"
