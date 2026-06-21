package com.keepguard.ms_user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe principal MsUserApplication
 * Teste simples para cobertura sem carregar contexto completo
 */
@DisplayName("Ms User Application Tests")
class MsUserApplicationTest {
    
    @Test
    @DisplayName("Deve verificar se a classe principal existe")
    void shouldVerifyMainApplicationClassExists() {
        // Given
        Class<?> applicationClass = MsUserApplication.class;
        
        // When & Then
        assertNotNull(applicationClass, "Classe MsUserApplication deve existir");
        assertEquals("MsUserApplication", applicationClass.getSimpleName(), "Nome da classe deve ser correto");
        assertEquals("com.keepguard.ms_user", applicationClass.getPackageName(), "Package deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe principal tem método main")
    void shouldVerifyMainMethodExists() {
        // Given
        Class<?> applicationClass = MsUserApplication.class;
        
        try {
            // When
            applicationClass.getDeclaredMethod("main", String[].class);
            
            // Then
            assertTrue(true, "Método main encontrado na classe principal");
        } catch (NoSuchMethodException e) {
            fail("Método main não encontrado na classe principal");
        }
    }
    
    @Test
    @DisplayName("Deve verificar anotações da classe principal")
    void shouldVerifyMainApplicationAnnotations() {
        // Given
        Class<?> applicationClass = MsUserApplication.class;
        
        // When & Then
        assertTrue(applicationClass.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class),
                "Classe deve ter anotação @SpringBootApplication");
        assertTrue(applicationClass.isAnnotationPresent(org.springframework.data.jpa.repository.config.EnableJpaAuditing.class),
                "Classe deve ter anotação @EnableJpaAuditing");
        assertTrue(applicationClass.isAnnotationPresent(org.springframework.context.annotation.Import.class),
                "Classe deve ter anotação @Import");
    }
    
    @Test
    @DisplayName("Deve verificar configuração do SpringBootApplication")
    void shouldVerifySpringBootApplicationConfiguration() {
        // Given
        org.springframework.boot.autoconfigure.SpringBootApplication annotation = 
            MsUserApplication.class.getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);
        
        // When & Then
        assertNotNull(annotation, "Anotação @SpringBootApplication deve existir");
        
        String[] scanBasePackages = annotation.scanBasePackages();
        assertNotNull(scanBasePackages, "scanBasePackages deve estar configurado");
        assertEquals(3, scanBasePackages.length, "Deve ter 3 pacotes base");
        assertEquals("com.keepguard.ms_user", scanBasePackages[0], "Primeiro pacote base deve ser correto");
        assertEquals("com.keepguard.lib_common", scanBasePackages[1], "Segundo pacote base deve ser correto");
        assertEquals("com.keepguard.lib_security", scanBasePackages[2], "Terceiro pacote base deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar configuração do Import")
    void shouldVerifyImportConfiguration() {
        // Given
        org.springframework.context.annotation.Import annotation = 
            MsUserApplication.class.getAnnotation(org.springframework.context.annotation.Import.class);
        
        // When & Then
        assertNotNull(annotation, "Anotação @Import deve existir");
        
        Class<?>[] value = annotation.value();
        assertNotNull(value, "value deve estar configurado");
        assertEquals(1, value.length, "Deve ter 1 classe importada");
        assertTrue(java.util.Arrays.stream(value)
            .anyMatch(c -> c.getName().equals("com.keepguard.lib_common.config.MetricsConfig")), 
            "Deve importar MetricsConfig");
    }
    
    @Test
    @DisplayName("Deve verificar se o método main é estático e público")
    void shouldVerifyMainMethodIsStaticAndPublic() {
        // Given
        Class<?> applicationClass = MsUserApplication.class;
        
        try {
            // When
            java.lang.reflect.Method mainMethod = applicationClass.getDeclaredMethod("main", String[].class);
            
            // Then
            assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()), 
                "Método main deve ser estático");
            assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()), 
                "Método main deve ser público");
            assertEquals(String[].class, mainMethod.getParameterTypes()[0], 
                "Método main deve receber array de String");
            assertEquals(void.class, mainMethod.getReturnType(), 
                "Método main deve retornar void");
        } catch (NoSuchMethodException e) {
            fail("Método main não encontrado");
        }
    }
    
    @Test
    @DisplayName("Deve executar o método main sem erros")
    void shouldExecuteMainMethodWithoutErrors() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            // Given
            mockedSpringApplication.when(() -> SpringApplication.run(eq(MsUserApplication.class), any(String[].class)))
                    .thenReturn(null);
            
            // When & Then
            assertDoesNotThrow(() -> {
                MsUserApplication.main(new String[]{});
            });
            
            // Verify
            mockedSpringApplication.verify(() ->
                    SpringApplication.run(eq(MsUserApplication.class), any(String[].class))
            );
        }
    }
    
    @Test
    @DisplayName("Deve executar o método main com argumentos")
    void shouldExecuteMainMethodWithArguments() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            // Given
            mockedSpringApplication.when(() -> SpringApplication.run(eq(MsUserApplication.class), any(String[].class)))
                    .thenReturn(null);
            String[] args = {"--spring.profiles.active=test", "--server.port=8082"};
            
            // When & Then
            assertDoesNotThrow(() -> {
                MsUserApplication.main(args);
            });
            
            // Verify
            mockedSpringApplication.verify(() ->
                    SpringApplication.run(MsUserApplication.class, args)
            );
        }
    }
    
    @Test
    @DisplayName("Deve executar o método main com argumentos nulos")
    void shouldExecuteMainMethodWithNullArguments() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            // Given
            mockedSpringApplication.when(() -> SpringApplication.run(eq(MsUserApplication.class), any(String[].class)))
                    .thenReturn(null);
            
            // When & Then
            assertDoesNotThrow(() -> {
                MsUserApplication.main(null);
            });
            
            // Verify
            mockedSpringApplication.verify(() ->
                    SpringApplication.run(MsUserApplication.class, (String[]) null)
            );
        }
    }
    
    @Test
    @DisplayName("Deve verificar se a classe pode ser carregada pelo ClassLoader")
    void shouldVerifyClassCanBeLoadedByClassLoader() {
        // Given
        String className = "com.keepguard.ms_user.MsUserApplication";
        
        // When
        Class<?> loadedClass = null;
        try {
            loadedClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            fail("Classe não pode ser carregada: " + e.getMessage());
        }
        
        // Then
        assertNotNull(loadedClass, "Classe deve ser carregada com sucesso");
        assertEquals(MsUserApplication.class, loadedClass, "Classe carregada deve ser a mesma");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe pode ser instanciada")
    void shouldVerifyClassCanBeInstantiated() {
        // Given & When
        MsUserApplication application = new MsUserApplication();
        
        // Then
        assertNotNull(application, "MsUserApplication deve ser instanciável");
        assertEquals(MsUserApplication.class, application.getClass(), "Tipo da classe deve ser correto");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe tem construtor padrão")
    void shouldVerifyClassHasDefaultConstructor() {
        // Given
        Class<?> applicationClass = MsUserApplication.class;
        
        try {
            // When
            java.lang.reflect.Constructor<?> constructor = applicationClass.getDeclaredConstructor();
            
            // Then
            assertNotNull(constructor, "Construtor padrão deve existir");
            assertTrue(java.lang.reflect.Modifier.isPublic(constructor.getModifiers()), 
                "Construtor padrão deve ser público");
        } catch (NoSuchMethodException e) {
            fail("Construtor padrão não encontrado");
        }
    }
    
    @Test
    @DisplayName("Deve verificar se a classe é final")
    void shouldVerifyClassIsNotFinal() {
        // Given
        Class<?> applicationClass = MsUserApplication.class;
        
        // When & Then
        assertFalse(java.lang.reflect.Modifier.isFinal(applicationClass.getModifiers()), 
            "Classe principal não deve ser final para permitir extensão");
    }
    
    @Test
    @DisplayName("Deve verificar se a classe é pública")
    void shouldVerifyClassIsPublic() {
        // Given
        Class<?> applicationClass = MsUserApplication.class;
        
        // When & Then
        assertTrue(java.lang.reflect.Modifier.isPublic(applicationClass.getModifiers()), 
            "Classe principal deve ser pública");
    }
}
