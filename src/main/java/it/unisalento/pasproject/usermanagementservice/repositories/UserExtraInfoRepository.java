package it.unisalento.pasproject.usermanagementservice.repositories;

import it.unisalento.pasproject.usermanagementservice.domain.UserExtraInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserExtraInfoRepository extends MongoRepository<UserExtraInfo, String> {
    UserExtraInfo findByUserId(String userId);
}
