package com.albertobuituron.literalura;
import com.albertobuituron.literalura.main.Main;
import com.albertobuituron.literalura.repository.AuthorRepository;
import com.albertobuituron.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class LiterAluraAplicacion implements CommandLineRunner
{

	public static void main(String[] args)
	{
		SpringApplication.run(LiterAluraAplicacion.class, args);
		}

	@Autowired
	private LibroRepository repository;
	@Autowired
	private AuthorRepository authorRepository;

	public void run(String[] args) {
		Main principal = new Main(repository,authorRepository);
		principal.showMenu();
	}
}
