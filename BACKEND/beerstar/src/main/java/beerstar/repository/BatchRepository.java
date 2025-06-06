package beerstar.repository;

import beerstar.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Cambiado a List

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {


    List<Batch> findByProvider_Id(Long providerId); // Esta es la forma más robusta para propiedades anidadas
}