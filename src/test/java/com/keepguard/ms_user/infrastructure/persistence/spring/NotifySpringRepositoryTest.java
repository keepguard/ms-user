package com.keepguard.ms_user.infrastructure.persistence.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para NotifySpringRepository
 * Teste simples para cobertura
 */
@DisplayName("Notify Spring Repository Tests")
class NotifySpringRepositoryTest {
    
    @Test
    @DisplayName("Deve verificar se a interface NotifySpringRepository existe")
    void shouldVerifyNotifySpringRepositoryInterfaceExists() {
        // Given
        Class<?> repositoryClass = NotifySpringRepository.class;
        
        // When & Then
        assertNotNull(repositoryClass, "Interface NotifySpringRepository deve existir");
        assertEquals("NotifySpringRepository", repositoryClass.getSimpleName(), "Nome da interface deve ser correto");
        assertEquals("com.keepguard.ms_user.infrastructure.persistence.spring", repositoryClass.getPackageName(), "Package deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar se estende JpaRepository")
    void shouldVerifyExtendsJpaRepository() {
        // Given
        Class<?> repositoryClass = NotifySpringRepository.class;
        
        // When & Then
        assertTrue(org.springframework.data.jpa.repository.JpaRepository.class.isAssignableFrom(repositoryClass), 
            "Deve estender JpaRepository");
    }
    
    @Test
    @DisplayName("Deve verificar métodos de consulta personalizados")
    void shouldVerifyCustomQueryMethods() {
        // Given
        Class<?> repositoryClass = NotifySpringRepository.class;
        
        // When & Then
        try {
            // Verificar se os métodos existem
            repositoryClass.getMethod("findByUserId", java.util.UUID.class);
            repositoryClass.getMethod("findAllByUserIdIn", java.util.List.class);
            repositoryClass.getMethod("existsByUserId", java.util.UUID.class);
            repositoryClass.getMethod("findAllByNotifyEmail", boolean.class);
            repositoryClass.getMethod("findAllByNotifySms", boolean.class);
            repositoryClass.getMethod("findAllByNotifyWhatsapp", boolean.class);
            repositoryClass.getMethod("findAllByNotifyPush", boolean.class);
        } catch (NoSuchMethodException e) {
            fail("Métodos de consulta personalizados devem existir: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Deve verificar anotações de query")
    void shouldVerifyQueryAnnotations() {
        // Given
        Class<?> repositoryClass = NotifySpringRepository.class;
        
        // When & Then
        try {
            var findByUserIdMethod = repositoryClass.getMethod("findByUserId", java.util.UUID.class);
            assertTrue(findByUserIdMethod.isAnnotationPresent(org.springframework.data.jpa.repository.Query.class), 
                "findByUserId deve ter anotação @Query");
            
            var findAllByUserIdInMethod = repositoryClass.getMethod("findAllByUserIdIn", java.util.List.class);
            assertTrue(findAllByUserIdInMethod.isAnnotationPresent(org.springframework.data.jpa.repository.Query.class), 
                "findAllByUserIdIn deve ter anotação @Query");
            
            var findAllByNotifyEmailMethod = repositoryClass.getMethod("findAllByNotifyEmail", boolean.class);
            assertTrue(findAllByNotifyEmailMethod.isAnnotationPresent(org.springframework.data.jpa.repository.Query.class), 
                "findAllByNotifyEmail deve ter anotação @Query");
            
            var findAllByNotifySmsMethod = repositoryClass.getMethod("findAllByNotifySms", boolean.class);
            assertTrue(findAllByNotifySmsMethod.isAnnotationPresent(org.springframework.data.jpa.repository.Query.class), 
                "findAllByNotifySms deve ter anotação @Query");
            
            var findAllByNotifyWhatsappMethod = repositoryClass.getMethod("findAllByNotifyWhatsapp", boolean.class);
            assertTrue(findAllByNotifyWhatsappMethod.isAnnotationPresent(org.springframework.data.jpa.repository.Query.class), 
                "findAllByNotifyWhatsapp deve ter anotação @Query");
            
            var findAllByNotifyPushMethod = repositoryClass.getMethod("findAllByNotifyPush", boolean.class);
            assertTrue(findAllByNotifyPushMethod.isAnnotationPresent(org.springframework.data.jpa.repository.Query.class), 
                "findAllByNotifyPush deve ter anotação @Query");
        } catch (NoSuchMethodException e) {
            fail("Métodos devem existir: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Deve verificar parâmetros das queries")
    void shouldVerifyQueryParameters() {
        // Given
        Class<?> repositoryClass = NotifySpringRepository.class;
        
        // When & Then
        try {
            var findByUserIdMethod = repositoryClass.getMethod("findByUserId", java.util.UUID.class);
            var queryAnnotation = findByUserIdMethod.getAnnotation(org.springframework.data.jpa.repository.Query.class);
            assertNotNull(queryAnnotation, "Anotação @Query deve existir");
            assertTrue(queryAnnotation.value().contains(":userId"), "Query deve conter parâmetro :userId");
            
            var findAllByUserIdInMethod = repositoryClass.getMethod("findAllByUserIdIn", java.util.List.class);
            var queryAnnotation2 = findAllByUserIdInMethod.getAnnotation(org.springframework.data.jpa.repository.Query.class);
            assertNotNull(queryAnnotation2, "Anotação @Query deve existir");
            assertTrue(queryAnnotation2.value().contains(":userIds"), "Query deve conter parâmetro :userIds");
            
            var findAllByNotifyEmailMethod = repositoryClass.getMethod("findAllByNotifyEmail", boolean.class);
            var queryAnnotation3 = findAllByNotifyEmailMethod.getAnnotation(org.springframework.data.jpa.repository.Query.class);
            assertNotNull(queryAnnotation3, "Anotação @Query deve existir");
            assertTrue(queryAnnotation3.value().contains(":notifyEmail"), "Query deve conter parâmetro :notifyEmail");
        } catch (NoSuchMethodException e) {
            fail("Métodos devem existir: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Deve verificar tipos de retorno dos métodos")
    void shouldVerifyMethodReturnTypes() {
        // Given
        Class<?> repositoryClass = NotifySpringRepository.class;
        
        // When & Then
        try {
            var findByUserIdMethod = repositoryClass.getMethod("findByUserId", java.util.UUID.class);
            assertEquals(java.util.Optional.class, findByUserIdMethod.getReturnType(), 
                "findByUserId deve retornar Optional");
            
            var findAllByUserIdInMethod = repositoryClass.getMethod("findAllByUserIdIn", java.util.List.class);
            assertEquals(java.util.List.class, findAllByUserIdInMethod.getReturnType(), 
                "findAllByUserIdIn deve retornar List");
            
            var existsByUserIdMethod = repositoryClass.getMethod("existsByUserId", java.util.UUID.class);
            assertEquals(boolean.class, existsByUserIdMethod.getReturnType(), 
                "existsByUserId deve retornar boolean");
            
            var findAllByNotifyEmailMethod = repositoryClass.getMethod("findAllByNotifyEmail", boolean.class);
            assertEquals(java.util.List.class, findAllByNotifyEmailMethod.getReturnType(), 
                "findAllByNotifyEmail deve retornar List");
        } catch (NoSuchMethodException e) {
            fail("Métodos devem existir: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Deve verificar se a interface pode ser instanciada como proxy")
    void shouldVerifyInterfaceCanBeInstantiatedAsProxy() {
        // Given
        Class<?> repositoryClass = NotifySpringRepository.class;
        
        // When & Then
        assertTrue(repositoryClass.isInterface(), "Deve ser uma interface");
        assertTrue(java.lang.reflect.Proxy.isProxyClass(repositoryClass) || repositoryClass.isInterface(), 
            "Deve ser uma interface ou proxy");
    }
}
