package yh729_bean;

/**
 * Created by Yanhao on 15-7-29.
 */
public class my_remindBean {
    private int PhotoDrawableId;
    private String Title;
    private String Content;
    private int num;
    private long Time;
    private boolean important;
    private String Type;
    public my_remindBean(int photoDrawableId, String messageTitle,
                         String messageContent, long messageTime,
                         String messageType,int num) {
        PhotoDrawableId = photoDrawableId;
        Title = messageTitle;
        Content = messageContent;
        Time = messageTime;
        Type= messageType;
        this.num=num;
        important=false;
    }

    public int getPhotoDrawableId() {
        return PhotoDrawableId;
    }
    public void setPhotoDrawableId(int mPhotoDrawableId) {
        this.PhotoDrawableId = mPhotoDrawableId;
    }
    public String getMessageTitle() {
        return Title;
    }
    public void setMessageTitle(String messageTitle) {
        Title = messageTitle;
    }
    public String getMessageContent() {
        return Content;
    }
    public void setMessageContent(String messageContent) {
        Content = messageContent;
    }
    public long getMessageTime() {
        return Time;
    }
    public void setMessageTime(long messageTime) {
        Time = messageTime;
    }
    public String getMessageType(){
        return Type;
    }
    public int getNum(){return num;}
    public void setNum(int n){
        num=n;
    }
    public boolean getImportant(){return important;}
    public void setImportant(boolean i){
        important=i;
    }
    @Override
    public String toString() {
        return "my_remindBean [mPhotoDrawableId=" + PhotoDrawableId
                + ", MessageName=" + Title + ", MessageContent="
                + Content + ", MessageTime=" + Time
                + "MessageType="+ Type+"]";
    }


}
