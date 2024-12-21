package com.devkobe24.kobe_bulletin_board.common.sevice;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
public class CommonService<T, ID> {
	private final JpaRepository<T, ID> repository;

	public CommonService(JpaRepository<T, ID> repository) {
		this.repository = repository;
	}

	public T findById(ID id, ResponseCode errorCode) {
		return repository.findById(id)
			.orElseThrow(() -> {
				log.error("{} not found with id {}", errorCode.name(), id);
				return new CustomException(errorCode);
			});
	}
}
