ALTER TABLE person
ADD COLUMN profile_url VARCHAR(255) DEFAULT 'https://some-profile-link.com',
ADD COLUMN photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg';