package kr.co.yeogiga.application.settlement.service;

import kr.co.yeogiga.application.settlement.dto.SettlementRequest;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import kr.co.yeogiga.domain.settlement.entity.Settlement;
import kr.co.yeogiga.domain.settlement.exception.SettlementErrorType;
import kr.co.yeogiga.domain.settlement.service.PayInfoService;
import kr.co.yeogiga.domain.settlement.service.SettlementService;
import kr.co.yeogiga.domain.settlement.type.SettlementType;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettlementCommandServiceTest {
    @Mock
    private SettlementService settlementService;
    
    @Mock
    private PayInfoService payInfoService;
    
    @Mock
    private TripMemberService tripMemberService;
    
    @InjectMocks
    private SettlementCommandService settlementCommandService;
    
    @Nested
    @DisplayName("정산 내역 생성")
    class CreateSettlement {
        private final Long tripId = 10L;
        private final Long userId = 1L;
        
        private List<User> members = List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build()
        );
        
        private SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                .name("점심 식사")
                .totalPrice(50000L)
                .date(LocalDate.now())
                .type(SettlementType.RESTAURANT)
                .payers(List.of(
                        SettlementRequest.PayInfoDto.builder()
                                .userId(1L)
                                .price(10000L)
                                .isCompleted(true)
                                .build(),
                        SettlementRequest.PayInfoDto.builder()
                                .userId(2L)
                                .price(40000L)
                                .isCompleted(false)
                                .build()
                ))
                .build();
        
        @Captor
        ArgumentCaptor<Settlement> settlementCaptor;
        
        @Captor
        private ArgumentCaptor<List<PayInfo>> payInfosCaptor;
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            when(settlementService.save(any(Settlement.class))).thenReturn(100L);
            doNothing().when(payInfoService).saveAllInBatch(anyList());
            
            // when
            settlementCommandService.createSettlement(tripId, userId, settlementDto);
            
            // then
            verify(settlementService, times(1)).save(settlementCaptor.capture());
            verify(payInfoService, times(1)).saveAllInBatch(payInfosCaptor.capture());
            
            assertEquals(false, settlementCaptor.getValue().isCompleted());
            payInfosCaptor.getValue().forEach(payInfo -> assertEquals(100L, payInfo.getSettlementId()));
        }
        
        @Test
        @DisplayName("성공 - 모든 멤버가 정산이 완료된 경우 해당 정산 내역이 완료가 되는가")
        void successIfAllCompleted() {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.now())
                    .type(SettlementType.RESTAURANT)
                    .payers(List.of(
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(1L)
                                    .price(10000L)
                                    .isCompleted(true)
                                    .build(),
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(2L)
                                    .price(40000L)
                                    .isCompleted(true)
                                    .build()
                    ))
                    .build();
            
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            when(settlementService.save(any(Settlement.class))).thenReturn(100L);
            doNothing().when(payInfoService).saveAllInBatch(anyList());
            
            // when
            settlementCommandService.createSettlement(tripId, userId, settlementDto);
            
            // then
            verify(settlementService, times(1)).save(settlementCaptor.capture());
            verify(payInfoService, times(1)).saveAllInBatch(anyList());
            
            assertEquals(true, settlementCaptor.getValue().isCompleted());
        }
        
        @Test
        @DisplayName("실패 - 여행 미존재")
        void failIfTripNotFound() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(Collections.emptyList());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.createSettlement(tripId, userId, settlementDto));
            
            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 정산자에 여행 멤버가 아닌 사용자가 포함된 경우")
        void failIfExistsNotTripMember() {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.now())
                    .type(SettlementType.RESTAURANT)
                    .payers(List.of(
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(1L)
                                    .price(10000L)
                                    .isCompleted(true)
                                    .build(),
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(3L)
                                    .price(40000L)
                                    .isCompleted(false)
                                    .build()
                    ))
                    .build();
            
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.createSettlement(tripId, userId, settlementDto));
            
            // then
            assertEquals(TripMemberErrorType.EXISTS_NOT_MEMBER, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 정산 내역 총합 불일치")
        void failIfTotalPriceNotEquals() {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.now())
                    .type(SettlementType.RESTAURANT)
                    .payers(List.of(
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(1L)
                                    .price(10000L)
                                    .isCompleted(true)
                                    .build(),
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(2L)
                                    .price(20000L)
                                    .isCompleted(false)
                                    .build()
                    ))
                    .build();
            
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.createSettlement(tripId, userId, settlementDto));
            
            // then
            assertEquals(SettlementErrorType.NOT_VALID_PRICE, exception.getErrorType());
        }
    }
    
    @Nested
    @DisplayName("정산 내역 삭제")
    class DeleteSettlement {
        private final Long tripId = 1L;
        private final Long userId = 1L;
        private final Long settlementId = 1L;
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            when(settlementService.readPayerIdById(settlementId)).thenReturn(Optional.of(userId));
            doNothing().when(payInfoService).deleteBySettlementId(settlementId);
            doNothing().when(settlementService).deleteById(settlementId);
            
            // when
            settlementCommandService.deleteSettlement(tripId, userId, settlementId);
            
            // then
            verify(payInfoService, times(1)).deleteBySettlementId(settlementId);
            verify(settlementService, times(1)).deleteById(settlementId);
        }
        
        @Test
        @DisplayName("실패 - 요청자가 여행 멤버가 아닐 경우")
        void failIfNotMember() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(false);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.deleteSettlement(tripId, userId, settlementId));
            
            // then
            assertEquals(TripMemberErrorType.IS_NOT_MEMBER, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 정산 내역이 존재하지 않을 경우")
        void failIfSettlementNotFound() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            when(settlementService.readPayerIdById(settlementId)).thenReturn(Optional.empty());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.deleteSettlement(tripId, userId, settlementId));
            
            // then
            assertEquals(SettlementErrorType.NOT_FOUND, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 요청자가 정산 생성자가 아닌 경우")
        void failIfIsNotPayer() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            when(settlementService.readPayerIdById(settlementId)).thenReturn(Optional.of(2L));
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.deleteSettlement(tripId, userId, settlementId));
            
            // then
            assertEquals(SettlementErrorType.IS_NOT_PAYER, exception.getErrorType());
        }
    }
    
    @Nested
    @DisplayName("정산 내역 갱신")
    class UpdateSettlement {
        private final Long tripId = 1L;
        private final Long userId = 1L;
        private final Long settlementId = 1L;
        
        private Settlement settlement;
        private PayInfo payInfo1;
        private PayInfo payInfo2;
        
        private User member1;
        private User member2;
        private User member3;
        
        private SettlementRequest.SettlementDto settlementDto;
        
        @Captor
        ArgumentCaptor<List<Long>> deletedPayInfoIds;
        
        @Captor
        ArgumentCaptor<List<PayInfo>> addedPayInfos;
        
        @BeforeEach
        void setUp() {
            settlement = Settlement.builder()
                    .tripId(tripId)
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.of(2025, 10, 6))
                    .type(SettlementType.RESTAURANT)
                    .payerId(userId)
                    .isCompleted(false)
                    .build();
            
            ReflectionTestUtils.setField(settlement, "id", settlementId);
            
            payInfo1 = PayInfo.builder()
                    .userId(userId)
                    .price(20000L)
                    .isCompleted(true)
                    .settlementId(settlement.getId())
                    .build();
            
            payInfo2 = PayInfo.builder()
                    .userId(2L)
                    .price(30000L)
                    .isCompleted(false)
                    .settlementId(settlement.getId())
                    .build();
            
            ReflectionTestUtils.setField(payInfo1, "id", 1L);
            ReflectionTestUtils.setField(payInfo2, "id", 2L);
            
            member1 = User.builder()
                    .id(userId)
                    .nickname("nick1")
                    .build();
            
            member2 = User.builder()
                    .id(2L)
                    .nickname("nick2")
                    .build();
            
            member3 = User.builder()
                    .id(3L)
                    .nickname("nick3")
                    .build();
        }
        
        @Test
        @DisplayName("성공 - 새로운 분담자 추가 및 기존 분담자 삭제")
        void success() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(List.of(member1, member2, member3));
            when(settlementService.readById(settlementId)).thenReturn(Optional.of(settlement));
            when(payInfoService.readAllBySettlementId(settlementId)).thenReturn(List.of(payInfo1, payInfo2));
            
            List<SettlementRequest.PayInfoDto> payInfoDtos = List.of(
                    SettlementRequest.PayInfoDto.builder()
                            .userId(1L)
                            .price(10000L)
                            .isCompleted(true)
                            .build(),
                    SettlementRequest.PayInfoDto.builder()
                            .userId(3L)
                            .price(50000L)
                            .isCompleted(true)
                            .build()
            );
            
            settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사 - 2")
                    .date(LocalDate.of(2025, 10, 6))
                    .totalPrice(60000L)
                    .type(SettlementType.RESTAURANT)
                    .payers(payInfoDtos)
                    .build();
            
            // when
            settlementCommandService.updateSettlement(tripId, userId, settlementId, settlementDto);
            
            // then
            assertEquals(settlementDto.name(), settlement.getName());
            assertEquals(settlementDto.totalPrice(), settlement.getTotalPrice());
            assertTrue(settlement.isCompleted());
            
            assertEquals(10000L, payInfo1.getPrice());
            
            verify(payInfoService, times(1)).deleteByIds(deletedPayInfoIds.capture());
            verify(payInfoService, times(1)).saveAllInBatch(addedPayInfos.capture());
            
            assertEquals(payInfo2.getId(), deletedPayInfoIds.getValue().get(0));
            PayInfo payInfo = addedPayInfos.getValue().get(0);
            assertEquals(3L, payInfo.getUserId());
            assertEquals(50000L, payInfo.getPrice());
            assertTrue(payInfo.isCompleted());
        }
        
        @Test
        @DisplayName("성공 - 정산 내역만 수정된 경우")
        void successIfOnlySettlementChanged() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(List.of(member1, member2, member3));
            when(settlementService.readById(settlementId)).thenReturn(Optional.of(settlement));
            when(payInfoService.readAllBySettlementId(settlementId)).thenReturn(List.of(payInfo1, payInfo2));
            
            List<SettlementRequest.PayInfoDto> payInfoDtos = List.of(
                    SettlementRequest.PayInfoDto.builder()
                            .userId(userId)
                            .price(20000L)
                            .isCompleted(true)
                            .build(),
                    SettlementRequest.PayInfoDto.builder()
                            .userId(2L)
                            .price(30000L)
                            .isCompleted(false)
                            .build()
            );
            
            settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사 - new")
                    .date(LocalDate.of(2025, 10, 6))
                    .totalPrice(50000L)
                    .type(SettlementType.RESTAURANT)
                    .payers(payInfoDtos)
                    .build();
            
            // when
            settlementCommandService.updateSettlement(tripId, userId, settlementId, settlementDto);
            
            // then
            assertEquals(settlementDto.name(), settlement.getName());
            
            verify(payInfoService, never()).deleteByIds(anyList());
            verify(payInfoService, never()).saveAllInBatch(anyList());
        }
        
        @Test
        @DisplayName("실패 - 여행이 존재하지 않는 경우")
        void failIfTripNotFound() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(Collections.emptyList());
            
            List<SettlementRequest.PayInfoDto> payInfoDtos = List.of(
                    SettlementRequest.PayInfoDto.builder()
                            .userId(userId)
                            .price(20000L)
                            .isCompleted(true)
                            .build(),
                    SettlementRequest.PayInfoDto.builder()
                            .userId(2L)
                            .price(30000L)
                            .isCompleted(false)
                            .build()
            );
            
            settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사 - new")
                    .date(LocalDate.of(2025, 10, 6))
                    .totalPrice(50000L)
                    .type(SettlementType.RESTAURANT)
                    .payers(payInfoDtos)
                    .build();
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.updateSettlement(tripId, userId, settlementId, settlementDto));
        
            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 해당 정산 내역의 작성자가 아닌 경우")
        void failIfNotPayer() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(List.of(member1, member2, member3));
            when(settlementService.readById(settlementId)).thenReturn(Optional.of(settlement));
            
            List<SettlementRequest.PayInfoDto> payInfoDtos = List.of(
                    SettlementRequest.PayInfoDto.builder()
                            .userId(userId)
                            .price(20000L)
                            .isCompleted(true)
                            .build(),
                    SettlementRequest.PayInfoDto.builder()
                            .userId(2L)
                            .price(30000L)
                            .isCompleted(false)
                            .build()
            );
            
            settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사 - new")
                    .date(LocalDate.of(2025, 10, 6))
                    .totalPrice(50000L)
                    .type(SettlementType.RESTAURANT)
                    .payers(payInfoDtos)
                    .build();
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.updateSettlement(tripId, 2L, settlementId, settlementDto));
            
            // then
            assertEquals(SettlementErrorType.IS_NOT_PAYER, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 여행 멤버가 아닌 분담자가 존재하는 경우")
        void failIfExistsNotMember() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(List.of(member1, member2, member3));
            when(settlementService.readById(settlementId)).thenReturn(Optional.of(settlement));
            
            List<SettlementRequest.PayInfoDto> payInfoDtos = List.of(
                    SettlementRequest.PayInfoDto.builder()
                            .userId(userId)
                            .price(20000L)
                            .isCompleted(true)
                            .build(),
                    SettlementRequest.PayInfoDto.builder()
                            .userId(4L)
                            .price(30000L)
                            .isCompleted(false)
                            .build()
            );
            
            settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사 - new")
                    .date(LocalDate.of(2025, 10, 6))
                    .totalPrice(50000L)
                    .type(SettlementType.RESTAURANT)
                    .payers(payInfoDtos)
                    .build();
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.updateSettlement(tripId, userId, settlementId, settlementDto));
            
            // then
            assertEquals(TripMemberErrorType.EXISTS_NOT_MEMBER, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 정산 내역 금액의 총합이 일치하지 않는 경우")
        void failIfNotValidPrice() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(List.of(member1, member2, member3));
            when(settlementService.readById(settlementId)).thenReturn(Optional.of(settlement));
            
            List<SettlementRequest.PayInfoDto> payInfoDtos = List.of(
                    SettlementRequest.PayInfoDto.builder()
                            .userId(userId)
                            .price(20000L)
                            .isCompleted(true)
                            .build(),
                    SettlementRequest.PayInfoDto.builder()
                            .userId(2L)
                            .price(40000L)
                            .isCompleted(false)
                            .build()
            );
            
            settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사 - new")
                    .date(LocalDate.of(2025, 10, 6))
                    .totalPrice(50000L)
                    .type(SettlementType.RESTAURANT)
                    .payers(payInfoDtos)
                    .build();
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.updateSettlement(tripId, userId, settlementId, settlementDto));
            
            // then
            assertEquals(SettlementErrorType.NOT_VALID_PRICE, exception.getErrorType());
        }
    }
}
