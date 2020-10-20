package com.Whowant.Tokki.VO;

import java.util.ArrayList;

public class FriendVO {

    public ArrayList<FriendInviteVO> friendInviteVOArrayList;

    // 친구 초대
    public static class FriendInviteVO {
        String image;
        String name;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
