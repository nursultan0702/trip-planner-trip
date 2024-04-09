package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.repository.MemberRepository;
import com.tripplannertrip.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public Set<MemberEntity> getMembers(List<String> emails) {
        Set<MemberEntity> members = new HashSet<>();
        emails.forEach(email -> memberRepository.findById(email).ifPresentOrElse(
                members::add,
                () -> {
                    MemberEntity newMember = new MemberEntity();
                    newMember.setEmail(email);
                    MemberEntity savedMember = memberRepository.save(newMember);
                    members.add(savedMember);
                }
        ));
        return members;
    }
}
