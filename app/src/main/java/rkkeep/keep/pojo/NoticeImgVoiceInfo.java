package rkkeep.keep.pojo;

/**
 * 图片跟录音只能包含其中的一种
 */
public class NoticeImgVoiceInfo {

    //图片的路径
    public String imagePic;

    //语音的路径
    public String voicePic;

    //语音的长度（毫秒值）
    public long length;

    public NoticeImgVoiceInfo(String imagePic) {
        this.imagePic = imagePic;
    }

    public NoticeImgVoiceInfo(String voicePic, long length) {
        this.voicePic = voicePic;
        this.length = length;
    }

}
