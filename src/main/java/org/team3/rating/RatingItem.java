package org.team3.rating;

public class RatingItem {

    private String id;
    private String userId;
    private String productId;
    private String timestamp;
    private String locationName;
    private Integer rating;
    private String userNotes;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getLocationName() {
        return locationName;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getUserNotes() {
        return userNotes;
    }
    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }
    @Override
    public String toString() {
        return "RatingItem [id=" + id + ", locationName=" + locationName + ", productId=" + productId + ", rating="
                + rating + ", timestamp=" + timestamp + ", userId=" + userId + ", userNotes=" + userNotes + "]";
    }
}
