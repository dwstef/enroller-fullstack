<template>
    <div>
        <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>

    <span v-if="meetings.length == 0">
               Brak zaplanowanych spotkań.
           </span>
    <h3 v-else>
        Zaplanowane zajęcia ({{ meetings.length }})
    </h3>

    <meetings-list
            :meetings = "meetings"
            :username="username"
    @attend="addMeetingParticipant($event)"
    @unattend="removeMeetingParticipant($event)"
    @delete="deleteMeeting($event)"></meetings-list>
        </div>
        </template>

<script>
    import NewMeetingForm from "./NewMeetingForm";
    import MeetingsList from "./MeetingsList";
    export default {
        components: {NewMeetingForm, MeetingsList},
        props: ['username'],
        data() {
            return {
                meetings: [],
                newMeetings: []
            };
        },
        methods: {
            addNewMeeting(meeting) {
                 this.$http.post('meetings', meeting)
                     .then(response => {
                        this.meetings.push(meeting);
                        window.location.reload(true);
                     })
                     .catch(response => {
                     });
            },
            addMeetingParticipant(meeting) {
                meeting.participants.push(this.username);
            },
            removeMeetingParticipant(meeting) {
                meeting.participants.splice(meeting.participants.indexOf(this.username), 1);
            },
            deleteMeeting(meeting) {
                this.$http.delete('meetings/'+ meeting.id)
                     .then(response => {
                     this.meetings.splice(this.meetings.indexOf(meeting), 1);

                     })
                     .catch(response => {
                     });
            }
                    },
            mounted() {
                  this.$http.get('meetings')
                         .then(response=>{
                            this.meetings = response.body
                             console.log(response.body)
                         })
                         .catch(response => {
                         });
            }
    }
</script>