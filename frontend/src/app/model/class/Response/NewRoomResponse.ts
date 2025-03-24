export class NewRoomResponse{
    roomName!: string
    location!: string
    description!: string
    capacity!: number
    equipmentsIDs: number[] = []
}