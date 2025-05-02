package com.example.bookclub.data.repository

import com.example.bookclub.data.dao.BookClubDao
import com.example.bookclub.data.model.BookClub
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookClubRepository @Inject constructor(
    private val bookClubDao: BookClubDao
) {
    fun getAllClubs(): Flow<List<BookClub>> = bookClubDao.getAllClubs()

    fun getPublicClubs(): Flow<List<BookClub>> = bookClubDao.getPublicClubs()

    fun getClubById(clubId: Long): Flow<BookClub?> = bookClubDao.getClubById(clubId)

    // New method for HomeViewModel
    fun getUserClubs(userId: Long): Flow<List<BookClub>> {
        // This would normally use a join table between clubs and members
        // For now, using the existing getClubsByMember method
        return getClubsByMember(userId)
    }

    suspend fun insertClub(club: BookClub): Long = bookClubDao.insertClub(club)

    suspend fun updateClub(club: BookClub) = bookClubDao.updateClub(club)

    suspend fun deleteClub(club: BookClub) = bookClubDao.deleteClub(club)

    fun searchClubs(query: String): Flow<List<BookClub>> = bookClubDao.searchClubs(query)

    fun getClubsByMember(userId: Long): Flow<List<BookClub>> {
        // This would normally use a join table between clubs and members
        // For now, just return all clubs as a placeholder
        return getAllClubs()
    }

    suspend fun addMember(clubId: Long, userId: Long) = bookClubDao.incrementMemberCount(clubId)

    suspend fun removeMember(clubId: Long, userId: Long) = bookClubDao.decrementMemberCount(clubId)

    // New joinClub method for HomeViewModel
    suspend fun joinClub(userId: Long, clubId: Long) {
        // In a real app, this would add an entry to a club_members join table
        // For now, just increment the member count
        addMember(clubId, userId)
    }

    // New leaveClub method for HomeViewModel
    suspend fun leaveClub(userId: Long, clubId: Long) {
        // In a real app, this would remove an entry from a club_members join table
        // For now, just decrement the member count
        removeMember(clubId, userId)
    }

    suspend fun addDiscussion(clubId: Long) = bookClubDao.incrementDiscussionCount(clubId)

    suspend fun updateCurrentBook(clubId: Long, bookId: Long?) {
        getClubById(clubId).collect { club ->
            club?.let {
                updateClub(it.copy(currentBookId = bookId))
            }
        }
    }

    suspend fun updateNextMeetingDate(clubId: Long, meetingDate: Long) {
        getClubById(clubId).collect { club ->
            club?.let {
                updateClub(it.copy(nextMeetingDate = Date(meetingDate)))
            }
        }
    }

    suspend fun scheduleMeeting(clubId: Long, meetingDate: Date) {
        getClubById(clubId).collect { club ->
            club?.let {
                updateClub(it.copy(nextMeetingDate = meetingDate))
            }
        }
    }
}