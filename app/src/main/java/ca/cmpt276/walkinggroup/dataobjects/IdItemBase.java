package ca.cmpt276.walkinggroup.dataobjects;

/**
 * Base class for all items that have an ID and href from the server.
 */
public class IdItemBase {
    // NOTE: Make numbers Long/Integer, not long/int because only the former will
    //       deserialize if the value is null from the server.
    private Long id;
    private Boolean hasFullData;
    private String href;

    public IdItemBase() {

    }


    // Check if full data
    // -------------------------------------------------------------------------------------------
    // Server often replies with stub objects instead of full data.
    // If server sends back just an ID then it's a stub; otherwise you have full data about
    // *this* object. Objects it refers to, such as other users or groups, may not be filled in
    // (and hence those will have hasFullData set to false for them).
    public Boolean hasFullData() {
        return hasFullData;
    }
    public void setHasFullData(Boolean hasFullData) {
        this.hasFullData = hasFullData;
    }

    // Basic User Data
    // -------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Link (unneeded, but send by server...)
    // -------------------------------------------------------------------------------------------
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }

}
