package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.repository.MemberRepository;
import com.tripplannertrip.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final MemberRepository memberRepository;

  @Override
  public Set<MemberEntity> getOrCreateMembers(Set<String> emails) {
    if (emails == null || emails.isEmpty()) {
      return Collections.emptySet();
    }

    Set<MemberEntity> members = new HashSet<>();
    emails.forEach(email -> {
      var member = memberRepository.findById(email)
          .orElseGet(() -> createAndSaveMember(email));
      members.add(member);
    });
    return members;
  }

  @Override
  public Set<MemberEntity> getMembers(Set<String> emails) {
    if (emails == null || emails.isEmpty()) {
      return Collections.emptySet();
    }

    Set<MemberEntity> members = new HashSet<>();
    emails.forEach(email -> {
      var member = memberRepository.findById(email)
          .orElseThrow(() -> new EntityNotFoundException(email));
      members.add(member);
    });
    return members;
  }


  private MemberEntity createAndSaveMember(String email) {
    MemberEntity newMember = MemberEntity.builder().email(email).build();
    return memberRepository.save(newMember);
  }
}
