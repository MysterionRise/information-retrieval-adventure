package org.mystic.model;

import java.util.Arrays;

public class User {
    private final String id;
    private final String name;
    private final String[] interests;

    public User(String id, String name, String... interests) {
        this.id = id;
        this.name = name;
        this.interests = interests.clone();
    }

    public String toJson() {
        return String.format("{\"name\":\"%s\",\"interests\":[%s]}", name,
                Arrays.stream(interests).map(tag -> "\"" + tag + "\"").reduce((p, c) -> p + ", " + c).get());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getInterests() {
        return interests;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", interests=" + Arrays.toString(interests) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(interests, user.interests);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(interests);
        return result;
    }
}