package models

/**
 * Created by L. on 30-Jan-15.
 */

import play.api.db.slick.Config.driver.simple._

case class Bid(bidPrice:Float, bidder:String)
case class ReceivedItem(uniqueName: String, reservedPrice:Float, auctioneer:String)

case class Item (id: Long, uniqueName: String, reservedPrice:Float, auctioneer:String, highestBid: Float, highestBidder: Option[String], state:Int)
{

  def submitBid(user:String, price:Float)(implicit s: Session) = {
    if ((highestBid < price) && (state == 0)) {
      val query = for (i <- TableQuery[Items] if i.id === id && i.highestBid < price) yield i
      val updatedItem = Item(id, uniqueName, reservedPrice, auctioneer, price, Some(user), state)
      query.update(updatedItem)
      Some(updatedItem)
    } else None
  }

  def callAuction(implicit s:Session) = {
    val x = if (highestBid <= reservedPrice) -1 else 1
    val query = for (i <- TableQuery[Items] if i.id === id) yield i.state
    query.update(x)
  }
}

class Items(tag:Tag) extends Table[Item] (tag, "items") {
  def id = column[Long]("item_id", O.PrimaryKey, O.AutoInc)

  def uniqueName = column[String]("unique_name", O.NotNull)
  def reservedPrice = column[Float]("reserved_price", O.NotNull)
  def auctioneer = column[String]("auctioneer", O.NotNull)

  def highestBid = column[Float]("highest_bid", O.Nullable)
  def highestBidder = column[Option[String]]("highest_bidder", O.Nullable)

  def state = column[Int]("is_active", O.NotNull) //note* isActive: 0 is available for bidding, 1 is sold, -1 is failure

  def * = (id, uniqueName, reservedPrice, auctioneer, highestBid, highestBidder, state) <> (Item.tupled, Item.unapply _)
}

object Items {
  def openItems = {
    TableQuery[Items].filter(_.state === 0)
  }

  def getWithId(id: Long)(implicit s:Session): Option[Item] = {
    TableQuery[Items].filter(_.id === id).firstOption
  }
  def create(tObj: Item)(implicit s: Session): Item = {
    (TableQuery[Items] returning TableQuery[Items].map(_.id) into ((o, id) => o.copy(id = id))) += tObj
  }
  def getWithName(name:String)(implicit s:Session): Option[Item] = {
    TableQuery[Items].filter(_.uniqueName === name).firstOption
  }

  def getWithIdOrCreate(id: Long, tObj: Item)(implicit s:Session): Item = getWithId(id).fold(create(tObj))(rObj => rObj)
  def getWithIdOrElse(id: Long)(f: => Item)(implicit s:Session): Item = getWithId(id).fold(f)(o => o)

  def getWithNameOrCreate(name: String, tObj: Item)(implicit s:Session): Item = getWithName(name).fold(create(tObj))(rObj => rObj)
  def getWithNameOrElse(name: String)(f: => Item)(implicit s:Session): Item = getWithName(name).fold(f)(o => o)
}