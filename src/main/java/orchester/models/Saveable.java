package orchester.models;

public interface Saveable {
    void load(String[] data);

    String save();
}
