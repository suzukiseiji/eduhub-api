package com.eduhub.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Classe para testar conexão com MongoDB na inicialização
 * Remove após confirmar que está funcionando
 */
@Component
public class MongoConnectionTest implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Testa conexão listando databases
            System.out.println("🔄 Testando conexão MongoDB...");

            String dbName = mongoTemplate.getDb().getName();
            System.out.println("✅ Conectado ao MongoDB!");
            System.out.println("📂 Database: " + dbName);

            // Lista collections existentes
            var collections = mongoTemplate.getCollectionNames();
            System.out.println("📋 Collections encontradas: " + collections);

        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}