package client;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Data {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String time_stamp;
	private long   seqnum;
	private String symbol;
	private String price;
	private String bid_price;
	private String offer_price;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(String time_stamp) {
		this.time_stamp = time_stamp;
	}
	public long getSeqnum() {
		return seqnum;
	}
	public void setSeqnum(long seqnum) {
		this.seqnum = seqnum;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getBid_price() {
		return bid_price;
	}
	public void setBid_price(String bid_price) {
		this.bid_price = bid_price;
	}
	public String getOffer_price() {
		return offer_price;
	}
	public void setOffer_price(String offer_price) {
		this.offer_price = offer_price;
	}
}
