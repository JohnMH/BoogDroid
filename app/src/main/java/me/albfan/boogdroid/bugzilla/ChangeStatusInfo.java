package me.albfan.boogdroid.bugzilla;

import me.albfan.boogdroid.general.StatusInfo;

/**
 * Control status change with info about comment required
 */
public class ChangeStatusInfo extends StatusInfo {
    Boolean commentRequired;

    public Boolean isCommentRequired() {
        return commentRequired;
    }

    public void setCommentRequired(Boolean commentRequired) {
        this.commentRequired = commentRequired;
    }
}
