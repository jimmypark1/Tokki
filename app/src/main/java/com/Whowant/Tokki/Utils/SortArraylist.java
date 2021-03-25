package com.Whowant.Tokki.Utils;

import com.Whowant.Tokki.VO.MessageThreadVO;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class SortArraylist implements Comparator<MessageThreadVO> {
    // 0 : 인기순, 1 : 최신순, 2 : 댓글 많은 순
  //  private int sortType;

    public SortArraylist() {
       // this.sortType = sortType;
    }

    @Override
    public int compare(MessageThreadVO o1, MessageThreadVO o2) {
        return o1.getCreatedDate().compareTo(o2.getCreatedDate())*(-1);

    }

}