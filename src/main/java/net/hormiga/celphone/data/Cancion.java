package net.hormiga.celphone.data;

public class Cancion {
    private String id;
    private String titulo;
    private String artista;
    private String url;

    public Cancion(String id, String titulo, String artista, String url) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArtista() {
        return artista;
    }

    public String getUrl() {
        return url;
    }
}

