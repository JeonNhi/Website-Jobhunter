package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.hoidanit.jobhunter.domain.Subscriber;

import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber,Long>, JpaSpecificationExecutor<Subscriber> {
   boolean existsByEmail(String email);

   Optional<Subscriber> findById(Long id);

   Subscriber findByEmail(String email);
}
