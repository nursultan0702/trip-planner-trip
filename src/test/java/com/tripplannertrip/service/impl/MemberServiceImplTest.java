package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MemberServiceImpl memberService;

  @Test
  void testGetMembersWithValidEmails() {
    Set<String> emails = new HashSet<>();
    emails.add("test@example.com");
    MemberEntity member = new MemberEntity();
    member.setEmail("test@example.com");

    when(memberRepository.findById(anyString())).thenReturn(Optional.of(member));

    Set<MemberEntity> result = memberService.getOrCreateMembers(emails);
    assertEquals(1, result.size());
    assertTrue(result.contains(member));
    verify(memberRepository, times(1)).findById("test@example.com");
  }

  @Test
  void testGetMembersWithUnknownEmail() {
    Set<String> emails = new HashSet<>();
    emails.add("unknown@example.com");
    MemberEntity newMember = new MemberEntity();
    newMember.setEmail("unknown@example.com");

    when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
    when(memberRepository.save(any(MemberEntity.class))).thenReturn(newMember);

    Set<MemberEntity> result = memberService.getOrCreateMembers(emails);
    assertEquals(1, result.size());
    assertTrue(result.contains(newMember));
    verify(memberRepository, times(1)).findById("unknown@example.com");
    verify(memberRepository, times(1)).save(any(MemberEntity.class));
  }

  @Test
  void testGetMembersWithNullInput() {
    Set<MemberEntity> result = memberService.getOrCreateMembers(null);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetMembersWithEmptySet() {
    Set<MemberEntity> result = memberService.getOrCreateMembers(new HashSet<>());
    assertTrue(result.isEmpty());
  }
}
