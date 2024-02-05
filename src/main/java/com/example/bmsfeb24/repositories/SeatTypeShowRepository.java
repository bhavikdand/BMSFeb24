package com.example.bmsfeb24.repositories;

import com.example.bmsfeb24.models.SeatTypeShow;
import com.example.bmsfeb24.models.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatTypeShowRepository extends JpaRepository<SeatTypeShow, Integer> {

    List<SeatTypeShow> findByShow(Show show);
}
