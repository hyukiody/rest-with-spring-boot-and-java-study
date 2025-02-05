package br.com.somestudy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.somestudy.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
