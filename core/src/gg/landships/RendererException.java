package gg.landships;

public class RendererException extends IllegalStateException {
    RendererException(String message) {
        super("LandshipsRenderer: " + message);
    }
}
