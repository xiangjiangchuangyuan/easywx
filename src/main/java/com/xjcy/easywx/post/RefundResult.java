package com.xjcy.easywx.post;

public class RefundResult {
	public String appid;
	public String mch_id;
	public String transaction_id;
	public String out_trade_no;
	public String refund_id;
	public String refund_fee;
	public String total_fee;
	public String cash_fee;
	public String cash_refund_fee;

	@Override
	public String toString()
	{
		return "Refundresult [appid=" + appid + ", mch_id=" + mch_id + ", transaction_id=" + transaction_id
				+ ", out_trade_no=" + out_trade_no + ", refund_id=" + refund_id + ", refund_fee=" + refund_fee
				+ ", total_fee=" + total_fee + ", cash_fee=" + cash_fee + ", cash_refund_fee=" + cash_refund_fee + "]";
	}
}
