slow grid sample
================

A sample project to demonstrate the slowness of the grid being generated (rendered) in the browser using the basic Vaadin template with the Vaadin 8.1.6 and 8.2.1 release.

Vaadin Issue #10232 on GitHub: https://github.com/vaadin/framework/issues/10232

After this issue was closed with improvements for the initial rendering of the grid in Pull Request: https://github.com/vaadin/framework/pull/10579

The updated sample project uses the released Vaadin 8.3.1 including the initial rendering fixes.
Our potential customer base uses Internet Explorer 11 which is the browser that renders the grid slowest.
Also the test scenario was changed to reflect a typical ListView in the application with 20 visible columns and 15 hidden columns.

Vaadin Issue #10656 on GitHub: https://github.com/vaadin/framework/issues/10656

Performance Results:
====================

- Run the application, run "mvn jetty:run" and open http://localhost:8080/ for manual tests.

or

- Run "mvn integration-test" for automated tests using Testbench (CVAL license required)

Performance Vaadin 8.3.1
------------------------
These are performance results for Vaadin 8.3.1 with the default scenario 20 columns, 15 hidden columns and 1000 rows on a fast machine with the application running locally: Windows 7 64 Bit OS with Intel Core i7 CPU @ 2.3 Ghz and 16 GB RAM

|Browser|fixed width columns|complex headers (filter row)|Grid Size (C,HC,R)|Render Time (ms)|
|-------|-------------------|----------------------------|------------------|----------------|
|IE|false|true|(20, 15, 1000)|4461|
|IE|false|true|(20, 15, 1000)|4497|
|IE|false|true|(20, 15, 1000)|4448|
|IE|false|true|(20, 15, 1000)|4437|
|FF|false|true|(20, 15, 1000)|3545|
|FF|false|true|(20, 15, 1000)|2921|
|FF|false|true|(20, 15, 1000)|3532|
|FF|false|true|(20, 15, 1000)|3124|
|FF|false|true|(20, 15, 1000)|3592|
|CH|false|true|(20, 15, 1000)|1241|
|CH|false|true|(20, 15, 1000)|1288|
|CH|false|true|(20, 15, 1000)|1188|
|CH|false|true|(20, 15, 1000)|1228|
|CH|false|true|(20, 15, 1000)|1186|

Not using the complex filter row in header:

|Browser|fixed width columns|complex headers (filter row)|Grid Size (C,HC,R)|Render Time (ms)|
|-------|-------------------|----------------------------|------------------|----------------|
|IE|false|false|(20, 15, 1000)|2802|
|IE|false|false|(20, 15, 1000)|2908|
|IE|false|false|(20, 15, 1000)|2906|

Additionally using fixed widths:

|Browser|fixed width columns|complex headers (filter row)|Grid Size (C,HC,R)|Render Time (ms)|
|-------|-------------------|----------------------------|------------------|----------------|
|IE|true|false|(20, 15, 1000)|2494|
|IE|true|false|(20, 15, 1000)|2409|
|IE|true|false|(20, 15, 1000)|2380|


Used browsers:

Chrome 63.0.3239.108 (64 bit)
Firefox 57.0.4 (64 Bit)
Internet Explorer 11.0.9600.18893 (64 Bit)

Used system setup:

Windows 7 64 Bit OS with Intel Core i7 CPU @ 2.3 Ghz and 16 GB RAM


Performance OLD
===============
Automated test results run locally on 64 Bit browsers on Windows 7 64 Bit OS

|Browser|Vaadin Version|Grid Size (C,HC,R)|Render Time (ms)|Request Time (ms)|
|-------|--------------|------------------|----------------|-----------------|
|CH|8.1.6|(11, 1, 1000)|1267|17|
|CH|8.1.6|(100, 0, 1000)|3059|48|
|CH|8.1.6|(100, 0, 3000)|2891|28|
|CH|8.1.6|(100, 20, 3000)|5768|33|
|FF|8.1.6|(11, 1, 1000)|2680|8|
|FF|8.1.6|(100, 0, 1000)|9020|27|
|FF|8.1.6|(100, 0, 3000)|8955|27|
|FF|8.1.6|(100, 20, 3000)|12082|34|
|IE|8.1.6|(11, 1, 1000)|2820|8|
|IE|8.1.6|(100, 0, 1000)|10368|28|
|IE|8.1.6|(100, 0, 3000)|10386|34|
|IE|8.1.6|(100, 20, 3000)|16607|25|
|CH|8.1.2|(11, 1, 1000)|1112|26|
|CH|8.1.2|(100, 0, 1000)|2949|43|
|CH|8.1.2|(100, 0, 3000)|2825|42|
|CH|8.1.2|(100, 20, 3000)|5673|30|
|FF|8.1.2|(11, 1, 1000)|2645|7|
|FF|8.1.2|(100, 0, 1000)|7992|20|
|FF|8.1.2|(100, 0, 3000)|8638|19|
|FF|8.1.2|(100, 20, 3000)|11250|31|
|IE|8.1.2|(11, 1, 1000)|2818|7|
|IE|8.1.2|(100, 0, 1000)|10385|22|
|IE|8.1.2|(100, 0, 3000)|10456|22|
|IE|8.1.2|(100, 20, 3000)|16722|33|
|CH|8.1.0|(11, 1, 1000)|1163|18|
|CH|8.1.0|(100, 0, 1000)|3050|37|
|CH|8.1.0|(100, 0, 3000)|2935|32|
|CH|8.1.0|(100, 20, 3000)|5640|34|
|FF|8.1.0|(11, 1, 1000)|2629|6|
|FF|8.1.0|(100, 0, 1000)|8983|25|
|FF|8.1.0|(100, 0, 3000)|8746|23|
|FF|8.1.0|(100, 20, 3000)|12563|25|
|IE|8.1.0|(11, 1, 1000)|3278|8|
|IE|8.1.0|(100, 0, 1000)|11667|50|
|IE|8.1.0|(100, 0, 3000)|11653|35|
|IE|8.1.0|(100, 20, 3000)|18712|36|
|CH|8.1.6|(11, 1, 1000)|1342|16|
|CH|8.1.6|(100, 0, 1000)|3370|51|
|CH|8.1.6|(100, 0, 3000)|3229|38|
|CH|8.1.6|(100, 20, 3000)|6326|41|
|FF|8.1.6|(11, 1, 1000)|3145|9|
|FF|8.1.6|(100, 0, 1000)|10035|27|
|FF|8.1.6|(100, 0, 3000)|10141|25|
|FF|8.1.6|(100, 20, 3000)|13929|36|
|IE|8.1.6|(11, 1, 1000)|3245|10|
|IE|8.1.6|(100, 0, 1000)|11804|50|
|IE|8.1.6|(100, 0, 3000)|11929|36|
|IE|8.1.6|(100, 20, 3000)|18875|41|
|-------|--------------|------------------|----------------|-----------------|
|CH|8.1.7|(10, 0, 1000)|1049|0|
|CH|8.1.7|(100, 0, 1000)|2837|0|
|CH|8.1.7|(50, 50, 1000)|6538|0|
|FF|8.1.7|(10, 0, 1000)|2148|0|
|FF|8.1.7|(100, 0, 1000)|6037|0|
|FF|8.1.7|(50, 50, 1000)|12238|0|
|IE|8.1.7|(10, 0, 1000)|2887|0|
|IE|8.1.7|(100, 0, 1000)|11041|0|
|IE|8.1.7|(50, 50, 1000)|19261|0|
|-------|--------------|------------------|----------------|-----------------|
|CH|8.2.1|(10, 0, 1000)|982|0|
|CH|8.2.1|(100, 0, 1000)|2720|0|
|CH|8.2.1|(50, 50, 1000)|1909|0|
|FF|8.2.1|(10, 0, 1000)|1792|0|
|FF|8.2.1|(100, 0, 1000)|6065|0|
|FF|8.2.1|(50, 50, 1000)|5029|0|
|IE|8.2.1|(10, 0, 1000)|2462|0|
|IE|8.2.1|(100, 0, 1000)|10637|0|
|IE|8.2.1|(50, 50, 1000)|7143|0|
|-------|--------------|------------------|----------------|-----------------|

Hint: Enabling vaadinProductionMode reports Request Time with 0 ms starting with 8.1.7 and 8.2.1 Vaadin.

Used browsers:

Chrome 58.0.3029.110 (64 bit)
Firefox 55.0.3 (64 Bit)
Internet Explorer 11.0.9600.18837 (64 Bit)

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
