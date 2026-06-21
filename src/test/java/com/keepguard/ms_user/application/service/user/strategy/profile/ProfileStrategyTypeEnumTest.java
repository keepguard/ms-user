package com.keepguard.ms_user.application.service.user.strategy.profile;

import com.keepguard.ms_user.domain.enums.UserTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para ProfileStrategyTypeEnum
 * Demonstra como usar o enum de forma type-safe
 */
class ProfileStrategyTypeEnumTest {
    
    @Nested
    @DisplayName("Testes de conversão de UserTypeEnum")
    class UserTypeConversionTests {
        
        @Test
        @DisplayName("Deve converter PERSON para PERSON_PROFILE")
        void shouldConvertPersonToPersonProfile() {
            // Given
            UserTypeEnum personType = UserTypeEnum.PERSON;
            
            // When
            ProfileStrategyTypeEnum result = ProfileStrategyTypeEnum.fromUserType(personType);
            
            // Then
            assertEquals(ProfileStrategyTypeEnum.PERSON_PROFILE, result);
            assertEquals(UserTypeEnum.PERSON, result.getUserType());
            assertEquals("PersonProfileStrategy", result.getStrategyName());
        }
        
        @Test
        @DisplayName("Deve converter COMPANY para COMPANY_PROFILE")
        void shouldConvertCompanyToCompanyProfile() {
            // Given
            UserTypeEnum companyType = UserTypeEnum.COMPANY;
            
            // When
            ProfileStrategyTypeEnum result = ProfileStrategyTypeEnum.fromUserType(companyType);
            
            // Then
            assertEquals(ProfileStrategyTypeEnum.COMPANY_PROFILE, result);
            assertEquals(UserTypeEnum.COMPANY, result.getUserType());
            assertEquals("CompanyProfileStrategy", result.getStrategyName());
        }
        
        @Test
        @DisplayName("Deve lançar exceção para tipo null")
        void shouldThrowExceptionForNullType() {
            // Given
            UserTypeEnum nullType = null;
            
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                ProfileStrategyTypeEnum.fromUserType(nullType);
            });
        }
    }
    
    @Nested
    @DisplayName("Testes de verificação de suporte")
    class SupportVerificationTests {
        
        @Test
        @DisplayName("Deve retornar true para PERSON")
        void shouldReturnTrueForPerson() {
            // Given
            UserTypeEnum personType = UserTypeEnum.PERSON;
            
            // When
            boolean isSupported = ProfileStrategyTypeEnum.isSupported(personType);
            
            // Then
            assertTrue(isSupported);
        }
        
        @Test
        @DisplayName("Deve retornar true para COMPANY")
        void shouldReturnTrueForCompany() {
            // Given
            UserTypeEnum companyType = UserTypeEnum.COMPANY;
            
            // When
            boolean isSupported = ProfileStrategyTypeEnum.isSupported(companyType);
            
            // Then
            assertTrue(isSupported);
        }
        
        @Test
        @DisplayName("Deve retornar false para tipo null")
        void shouldReturnFalseForNullType() {
            // Given
            UserTypeEnum nullType = null;
            
            // When
            boolean isSupported = ProfileStrategyTypeEnum.isSupported(nullType);
            
            // Then
            assertFalse(isSupported);
        }
    }
    
    @Nested
    @DisplayName("Testes de valores do enum")
    class EnumValueTests {
        
        @Test
        @DisplayName("Deve ter valores corretos para PERSON_PROFILE")
        void shouldHaveCorrectValuesForPersonProfile() {
            // Given
            ProfileStrategyTypeEnum personProfile = ProfileStrategyTypeEnum.PERSON_PROFILE;
            
            // Then
            assertEquals(UserTypeEnum.PERSON, personProfile.getUserType());
            assertEquals("PersonProfileStrategy", personProfile.getStrategyName());
        }
        
        @Test
        @DisplayName("Deve ter valores corretos para COMPANY_PROFILE")
        void shouldHaveCorrectValuesForCompanyProfile() {
            // Given
            ProfileStrategyTypeEnum companyProfile = ProfileStrategyTypeEnum.COMPANY_PROFILE;
            
            // Then
            assertEquals(UserTypeEnum.COMPANY, companyProfile.getUserType());
            assertEquals("CompanyProfileStrategy", companyProfile.getStrategyName());
        }
    }
}
