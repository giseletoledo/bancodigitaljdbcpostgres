package com.bancodigitalspring;

import com.bancodigitalspring.config.DatabaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BancodigitalspringApplication {

	public static void main(String[] args) {

		SpringApplication.run(BancodigitalspringApplication.class, args);

		// Criar tabelas no banco ao iniciar
		DatabaseConfig.criarTabelas();
	}

}
