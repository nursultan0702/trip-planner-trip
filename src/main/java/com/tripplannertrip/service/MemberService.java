package com.tripplannertrip.service;

import com.tripplannertrip.entity.MemberEntity;
import java.util.Set;

public interface MemberService {
    Set<MemberEntity> getMembers(Set<String> emails);
}
