package controllers

/**
 * Created by L. on 30-Jan-15.
 */

import models._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.Json

object Application extends Controller {


  val items = TableQuery[Items]
  implicit val recievedItemsFormat = Json.format[ReceivedItem]
  implicit val bidFormat = Json.format[Bid]

  //  implicit val itemsFormat = Json.format[Items]

  def index = DBAction { implicit rs =>
    Ok(views.html.index(items.list))
  }

  /**Displays a list of only open ads
   *
   * @return
   */
  def openItems = DBAction { implicit rs =>
    val itemList = Items.openItems
    Ok(views.html.index(itemList.list))
  }

  /**Given a user we display last actions on all items' latest actions (Clearly we prioritize security....)
   *
   * @param user - given auctioneer name
   * @return
   */
  def usersItems(user:String) = DBAction { implicit rs =>
    val itemList = TableQuery[Items].filter(_.auctioneer === user)
    Ok(views.html.item(itemList.list))
  }

  /**Adds item to auction through JSON
   * Checks to make sure name will be unique before adding
   * @return BadRequest if JSON is invalid, Unauthorized if name is already taken
   */
  def addItemsToAuction = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[ReceivedItem].asOpt.fold(BadRequest: Result) { rItem =>
      val item = Item.apply(0, rItem.uniqueName, rItem.reservedPrice, rItem.auctioneer, 0, None, 0)
      Items.getWithName(item.uniqueName).fold {
        Items.create(item); Redirect(routes.Application.index)
      } { x => Unauthorized}
    }
  }

  /**Place Bid with item id in URL and bid as JSON
   * Checks valid item id, parses json and adds if bid is higher
   * @param id - item id
   * @return BadRequest if invalid item, BadRequest if invalid Json, Unauthorized if item is not open
   */
  def placeBid(id:Long) = DBAction(parse.json) { implicit rs =>
    Items.getWithId(id).fold(BadRequest: Result) { item =>
    rs.request.body.validate[Bid].asOpt.fold(BadRequest: Result) { bid =>
        item.submitBid(bid.bidder, bid.bidPrice).getOrElse(Unauthorized:Result)
      Redirect(routes.Application.index)
      }
    }
  }

  /**Call the Auction on the item with id in URL and user as parameters
   * Checks the valid item id, checks if user is auctioneer then closes
   * @param id - item id
   * @param user - requesting user
   * @return BadRequest if invalid item, Unauthorized if user is not auctioneer
   */
  def callItem(id:Long, user:String) = DBAction { implicit rs=>
    Items.getWithId(id).fold(BadRequest:Result) { item =>
      if (item.auctioneer == user){ item.callAuction; Redirect(routes.Application.usersItems(user))} else Unauthorized }
  }

}