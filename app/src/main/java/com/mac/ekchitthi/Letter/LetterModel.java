package com.mac.ekchitthi.Letter;

public class LetterModel {
    private String sender_id,sender_letter,read_status,future_timestamp,sent_time,sender_name,from,to,ticketID,stamp_image,rece_number;

    public LetterModel() {
    }

    public LetterModel(String sender_id, String sender_letter, String read_status, String future_timestamp, String sent_time,String sender_name,String from,String ticketID,String stamp_image,String rece_number,String to) {
        this.sender_id = sender_id;
        this.sender_letter = sender_letter;
        this.read_status = read_status;
        this.future_timestamp = future_timestamp;
        this.sent_time = sent_time;
        this.sender_name=sender_name;
        this.from=from;
        this.ticketID=ticketID;
        this.stamp_image=stamp_image;
        this.rece_number=rece_number;
        this.to=to;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_letter() {
        return sender_letter;
    }

    public void setSender_letter(String sender_letter) {
        this.sender_letter = sender_letter;
    }

    public String getRead_status() {
        return read_status;
    }

    public void setRead_status(String read_status) {
        this.read_status = read_status;
    }

    public String getFuture_timestamp() {
        return future_timestamp;
    }

    public void setFuture_timestamp(String future_timestamp) {
        this.future_timestamp = future_timestamp;
    }

    public String getSent_time() {
        return sent_time;
    }

    public void setSent_time(String sent_time) {
        this.sent_time = sent_time;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getStamp_image() {
        return stamp_image;
    }

    public void setStamp_image(String stamp_image) {
        this.stamp_image = stamp_image;
    }

    public String getRece_number() {
        return rece_number;
    }

    public void setRece_number(String rece_number) {
        this.rece_number = rece_number;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
