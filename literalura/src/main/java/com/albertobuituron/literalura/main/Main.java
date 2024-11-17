package com.albertobuituron.literalura.main;

import com.albertobuituron.literalura.models.Author;
import com.albertobuituron.literalura.models.DatosLibro;
import com.albertobuituron.literalura.models.Libro;
import com.albertobuituron.literalura.repository.AuthorRepository;
import com.albertobuituron.literalura.repository.LibroRepository;
import com.albertobuituron.literalura.services.ConvierteDatos;
import com.albertobuituron.literalura.services.RequestAPI;

import java.util.List;
import java.util.Scanner;

public class Main {
    private RequestAPI requestAPI = new RequestAPI();
    private Scanner scanner = new Scanner(System.in);
    private String urlBase ="https://gutendex.com/books/";
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AuthorRepository authorRepository;
    private List<Libro> libros;
    private List<Author> autores;

    public Main(LibroRepository libroRepository, AuthorRepository authorRepository) {
        this.libroRepository = libroRepository;
        this.authorRepository = authorRepository;
    }

    // Mostrar el menu en consola
    public void showMenu()
    {
        var opcion = -1;
        while (opcion != 0){
            var menu ="""
                    ================================================================
                    =        * LiterAlura *  Busqueda de Libros y Autores          =
                    ================================================================
                    
                    Selecciona una opcion del menú: 
                    ================================================================
                    
                    1 - Buscar un libro
                    2 - Consultar libros buscados
                    3 - Consultar autores
                    4 - Consultar autores de un año especifico
                    5 - Consultar libros por lenguaje
                     
                    0 - Salir    
                    ================================================================
                    Opcion:           
                    """;

            try {
                System.out.println(menu);
                opcion = scanner.nextInt();
                scanner.nextLine();
            }catch (Exception e){

                System.out.println("Por favor, ingresa una opcion valida");
            }

            switch (opcion){
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    consultarLibros();
                    break;
                case 3:
                    consultarAutores();
                    break;
                case 4:
                    consultarAutoresPorAno();
                    break;
                case 5:
                    consultarLibrosLenguaje();
                    break;
                case 0:
                    System.out.println("Muchas gracias por utilizar LiterAlura, nos vemos pronto");
                    break;
                default:
                    System.out.println("Por favor, ingresa una opcion valida");
            }
        }
    }

    // Extrae los datos de un libro
    private DatosLibro getDatosLibro() {
        System.out.println("Por favor, ingrese el nombre del libro a buscar:");
        var busqueda = scanner.nextLine().toLowerCase().replace(" ","%20");
        var json = requestAPI.getData(urlBase +
                "?search=" +
                busqueda);

        DatosLibro datosLibro = convierteDatos.obtenerDatos(json, DatosLibro.class);
        return datosLibro;
    }

    // Busca un libro y guarda infromacion en la BD en sus tablas correspondientes
    private void buscarLibro()
    {
        DatosLibro datosLibro = getDatosLibro();

        try {
            Libro libro = new Libro(datosLibro.resultados().get(0));
            Author author = new Author(datosLibro.resultados().get(0).autorList().get(0));

            System.out.println("""
                    
                    ================================
                    =  Resultado del libro buscado =
                    ================================
                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        descargas: %s
                    ]
                    """.formatted(libro.getTitulo(),
                    libro.getAutor(),
                    libro.getLenguaje(),
                    libro.getDescargas().toString()));

            libroRepository.save(libro);
            authorRepository.save(author);

        }catch (Exception e){
            System.out.println("""
                    
                    =======================================
                    =  No se encontró el libro ingresado  =
                    =======================================
                    """);
        }

    }

    // Trae los libros guardados en la BD
    private void consultarLibros() {
        libros = libroRepository.findAll();
        libros.stream().forEach(l -> {
            System.out.println("""
                    =======================================
                    =          Datos  del  libro          =
                    =======================================

                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        Descargas: %s
                    """.formatted(l.getTitulo(),
                    l.getAutor(),
                    l.getLenguaje(),
                    l.getDescargas().toString()));
        });
    }

    // Trae todos los autores de los libros consultados en la BD
    private void consultarAutores() {
        autores = authorRepository.findAll();
        autores.stream().forEach(a -> {
            System.out.println("""
                    =======================================
                    =          Datos  del Autor           =
                    =======================================
    
                        Autor: %s
                        Año de Nacimiento: %s
                        Año de Muerte: %s
                    """.formatted(a.getAutor(),
                    a.getNacimiento().toString(),
                    a.getDefuncion().toString()));
        });
    }

    // Trae a los autores apartir de cierto año
    public void consultarAutoresPorAno()
    {
        System.out.println("Ingresa el año a partir del cual buscar:");
        var anoBusqueda = scanner.nextInt();
        scanner.nextLine();

        List<Author> authors = authorRepository.autorPorFecha(anoBusqueda);
        authors.forEach( a -> {
            System.out.println("""
                    =======================================
                    =   Autor vivo en el año consultado   =
                    =======================================
                    
                    Nombre: %s
                    Fecha de Nacimiento: %s
                    Fecha de Muerte: %s
                    """.formatted(a.getAutor(),a.getNacimiento().toString(),a.getDefuncion().toString()));
        });
    }


    private void consultarLibrosLenguaje()
    {
        System.out.println("""
                ================================================================    
                =   Selecciona el lenguaje de los libros que deseas consultar  =
                ================================================================
                
                1 - Ingles  - (EN)
                2 - Español  - (ES)
                """);

        try {

            var opcion2 = scanner.nextInt();
            scanner.nextLine();

            switch (opcion2) {
                case 1:
                    libros = libroRepository.findByLenguaje("en");
                    break;
                case 2:
                    libros = libroRepository.findByLenguaje("es");
                    break;

                default:
                    System.out.println("Por favor ingresa una opcion valida: 1 o 2");
            }

            libros.stream().forEach(l -> {
                System.out.println("""
                    ============================================
                    =   Libros escritos en idioma  consultado  =
                    ============================================
    
                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        Descargas: %s
                    """.formatted(l.getTitulo(),
                        l.getAutor(),
                        l.getLenguaje(),
                        l.getDescargas().toString()));
            });

        } catch (Exception e){
            System.out.println("Ingresa un valor valido");
        }
    }

    public static class Principal {
        private RequestAPI requestAPI = new RequestAPI();
        private Scanner scanner = new Scanner(System.in);
        private String urlBase ="https://gutendex.com/books/";
        private ConvierteDatos convierteDatos = new ConvierteDatos();
        private LibroRepository libroRepository;
        private AuthorRepository authorRepository;
        private List<Libro> libros;
        private List<Author> autores;

        public Principal(LibroRepository libroRepository, AuthorRepository authorRepository) {
            this.libroRepository = libroRepository;
            this.authorRepository = authorRepository;
        }

        // Mostrar el menu en consola
        public void showMenu()
        {
            var opcion = -1;
            while (opcion != 0){
                var menu ="""
                        ==================================================
                        =   * LiterAlura *  Busqueda de Libros y Autores =
                        ==================================================
                        
                        Por favor selecciona una opcion del siguiente menú: 
                        
                        1 - Buscar un libro
                        2 - Consultar libros buscados
                        3 - Consultar autores
                        4 - Consultar autores de un año especifico
                        5 - Consultar libros por lenguaje
                         
                        0 - Salir
                        ==================================================
                        Opcion:               
                        """;

                try {
                    System.out.println(menu);
                    opcion = scanner.nextInt();
                    scanner.nextLine();
                }catch (Exception e){

                    System.out.println("Por favor ingresa una opción válida");
                }

                switch (opcion){
                    case 1:
                        buscarLibro();
                        break;
                    case 2:
                        consultarLibros();
                        break;
                    case 3:
                        consultarAutores();
                        break;
                    case 4:
                        consultarAutoresPorAno();
                        break;
                    case 5:
                        consultarLibrosLenguaje();
                        break;
                    case 0:
                        System.out.println("Muchas gracias por haber utilizado LiterAlura");
                        break;
                    default:
                        System.out.println("Por favor ingresa una opción válida");
                }
            }
        }

        // Extrae los datos de un libro
        private DatosLibro getDatosLibro() {
            System.out.println("Ingrese el nombre del libro");
            var busqueda = scanner.nextLine().toLowerCase().replace(" ","%20");
            var json = requestAPI.getData(urlBase +
                    "?search=" +
                    busqueda);

            DatosLibro datosLibro = convierteDatos.obtenerDatos(json, DatosLibro.class);
            return datosLibro;
        }

        // Busca un libro y guarda infromacion en la BD en sus tablas correspondientes
        private void buscarLibro()
        {
            DatosLibro datosLibro = getDatosLibro();

            try {
                Libro libro = new Libro(datosLibro.resultados().get(0));
                Author author = new Author(datosLibro.resultados().get(0).autorList().get(0));

                System.out.println("""
                    =======================================
                    =          Datos  del  libro          =
                    =======================================

                            Titulo: %s
                            Autor: %s
                            Idioma: %s
                            Descargas: %s

                        """.formatted(libro.getTitulo(),
                        libro.getAutor(),
                        libro.getLenguaje(),
                        libro.getDescargas().toString()));

                libroRepository.save(libro);
                authorRepository.save(author);

            }catch (Exception e){
                System.out.println("No se encontró el libro con el título solicitado");
            }

        }

        // Trae los libros guardados en la BD
        private void consultarLibros() {
            libros = libroRepository.findAll();
            libros.stream().forEach(l -> {
                System.out.println("""
                    =======================================
                    =          Datos  del  libro          =
                    =======================================

                            Titulo: %s
                            Autor: %s
                            Idioma: %s
                            Descargas: %s
                    """.formatted(l.getTitulo(),
                        l.getAutor(),
                        l.getLenguaje(),
                        l.getDescargas().toString()));
            });
        }

        // Trae todos los autores de los libros consultados en la BD
        private void consultarAutores() {
            autores = authorRepository.findAll();
            autores.stream().forEach(a -> {
                System.out.println("""
                            Autor: %s
                            Año de Nacimiento: %s
                            Año de Muerte: %s
                        """.formatted(a.getAutor(),
                        a.getNacimiento().toString(),
                        a.getDefuncion().toString()));
            });
        }

        // Trae a los autores apartir de cierto año
        public void consultarAutoresPorAno()
        {
            System.out.println("Ingresa el año a partir del cual buscar:");
            var anoBusqueda = scanner.nextInt();
            scanner.nextLine();

            List<Author> authors = authorRepository.autorPorFecha(anoBusqueda);
            authors.forEach( a -> {
                System.out.println("""
                        Nombre: %s
                        Fecha de Nacimiento: %s
                        Fecha de Muerte: %s
                        """.formatted(a.getAutor(),a.getNacimiento().toString(),a.getDefuncion().toString()));
            });
        }


        private void consultarLibrosLenguaje()
        {
            System.out.println("""
                    ================================================================    
                    =    Selecciona el Idioma de los libros que deseas consultar   =  
                    ================================================================
                    1 - Ingles  - (EN)
                    2 - Español  -  (ES)
                    """);

            try {

                var opcion2 = scanner.nextInt();
                scanner.nextLine();

                switch (opcion2) {
                    case 1:
                        libros = libroRepository.findByLenguaje("en");
                        break;
                    case 2:
                        libros = libroRepository.findByLenguaje("es");
                        break;

                    default:
                        System.out.println("Por favor ingresa una opcion valida");
                }

                libros.stream().forEach(l -> {
                    System.out.println("""    
                            Titulo: %s
                            Autor: %s
                            Idioma: %s
                            Descargas: %s
                        """.formatted(l.getTitulo(),
                            l.getAutor(),
                            l.getLenguaje(),
                            l.getDescargas().toString()));
                });

            } catch (Exception e){
                System.out.println("Por favor ingresa un valor válido");
            }
        }
    }
}
