package com.assign.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.assign.model.TestTbVO;

@Repository
public interface TestRepository extends JpaRepository<TestTbVO, Long> {

	List<TestTbVO> findByTestCol1(@Param("testCol1") Integer testCol1);
	
	@Query("SELECT t FROM TestTbVO t WHERE t.testCol1 = :testCol1")
	List<TestTbVO> findByTestCol1Jpql(@Param("testCol1") Integer testCol1);

}
