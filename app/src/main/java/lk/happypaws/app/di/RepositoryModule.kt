package lk.happypaws.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lk.happypaws.app.data.repository.AuthRepositoryImpl
import lk.happypaws.app.data.repository.UserRepositoryImpl
import lk.happypaws.app.domain.repository.AuthRepository
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        healthRepositoryImpl: lk.happypaws.app.data.repository.HealthRepositoryImpl
    ): lk.happypaws.app.domain.repository.HealthRepository

}
