package com.daxueoo.shopnc.model;

/**
 * 话题的模型类
 * Created by user on 15-8-2.
 */
public class TopicMessage extends BaseMessage {

    String icon_url;
    String topic_title;
    String topic_content;
    String topic_views;
    String topic_replies;
    String topic_time;
    String topic_user_icon;

    public TopicMessage() {

    }

    @Override
    public String toString() {
        return this.topic_title + this.topic_content + this.topic_views + this.topic_replies + this.topic_time + this.icon_url;
    }

    public TopicMessage(String topic_title, String topic_content, String topic_views, String topic_time, String topic_replies) {
        this.topic_title = topic_title;
        this.topic_content = topic_content;
        this.topic_views = topic_views;
        this.topic_replies = topic_replies;
        this.topic_time = topic_time;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }

    public String getTopic_content() {
        return topic_content;
    }

    public void setTopic_content(String topic_content) {
        this.topic_content = topic_content;
    }

    public String getTopic_views() {
        return topic_views;
    }

    public void setTopic_views(String topic_views) {
        this.topic_views = topic_views;
    }

    public String getTopic_replies() {
        return topic_replies;
    }

    public void setTopic_replies(String topic_replies) {
        this.topic_replies = topic_replies;
    }

    public String getTopic_time() {
        return topic_time;
    }

    public void setTopic_time(String topic_time) {
        this.topic_time = topic_time;
    }

    public String getTopic_user_icon() {
        return topic_user_icon;
    }

    public void setTopic_user_icon(String topic_user_icon) {
        this.topic_user_icon = topic_user_icon;
    }

}
