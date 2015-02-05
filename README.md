Activator template for Play Framework and the Slick database access library
This template helps building a classic web app or a JSON API

Running Instructions:
-Install SBT if you haven't already (http://www.scala-sbt.org/)
-Navigate to directory via terminal / console
-Type: sbt compile
(Compiles sources; automatically runs update first)
-Type: sbt run
	(Runs main class wiht expected output:
	--- (running the application from SBT, auto-reloading is enabled) ---
	[info] play - Listening for HTTP on /0:0:0:0:0:0:0:0:9000 )
-Navigate browser to localhost:9000

-Download Postman (http://www.getpostman.com/) and import the postman files from the directory.
-OR Using the code and comments provided in .app/controllers/Application.scala and ./conf/routes (or just look below) to exercise the endpoints with a REST client of your choice.

Close Instructions:
Ctrl + D
Close browser.

API Enpoints

POST with (application/JSON) localhost:9000/insert		--ads item to auction
JSON structure
{
	"uniqueName":"[namehere]",
    "reservedPrice":[pricehere],
    "auctioneer":"[namehere]"
}

POST with (application/JSON) localhost:9000/items/1/bid 	-- bid on item with key = 1 (primary key auto inc for items change accordingly)
JSON structure
{
    "bidPrice":[pricehere],
    "bidder":"[namehere]"
}

POST localhost:9000/items/1/close?user=Gandalf		--Close auction with auctioneer of an item and key of that item(change '1' and Gandalf)

GET localhost:9000/items/open		--display all items open for auction

GET localhost:9000/items/Gandalf		--get all status of all items from user(change Gandalf)