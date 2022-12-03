package com.whyranoid.domain.usecase

import com.whyranoid.domain.model.Post
import com.whyranoid.domain.repository.AccountRepository
import com.whyranoid.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyPostUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(): Flow<List<Post>> {
        return postRepository.getMyPostFlow(accountRepository.getUid())
    }
}
