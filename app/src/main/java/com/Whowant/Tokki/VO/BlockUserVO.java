package com.Whowant.Tokki.VO;

public class BlockUserVO {
    private String blockId;
    private String blockName;

    private String blockPhoto;

    public BlockUserVO() {
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public void setBlockPhoto(String blockPhoto) {
        this.blockPhoto = blockPhoto;
    }

    public String getBlockId() {
        return blockId;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getBlockPhoto() {
        return blockPhoto;
    }
}
