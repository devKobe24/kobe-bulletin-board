package com.devkobe24.kobe_bulletin_board.domain.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "manager")
public class Manager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false, name = "manager_name")
	private String managerName;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@OneToOne(mappedBy = "manager", cascade = CascadeType.ALL)
	private ManagerCredentials managerCredentials;

	public void setCredentials(ManagerCredentials credentials) {
		this.managerCredentials = credentials;
	}
}
