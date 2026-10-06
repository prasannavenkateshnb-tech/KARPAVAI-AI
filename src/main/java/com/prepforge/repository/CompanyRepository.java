package com.prepforge.repository;
import com.prepforge.entity.Company; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CompanyRepository extends JpaRepository<Company,Long>{Optional<Company> findByNameIgnoreCase(String name); List<Company> findByNameContainingIgnoreCase(String q);}
