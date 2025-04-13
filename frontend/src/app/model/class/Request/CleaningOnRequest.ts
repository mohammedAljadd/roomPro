import { RoomRequest } from "./RoomRequest";

export class CleaningOnRequest{
    cleaningId!: number;
    room!: RoomRequest;
    requestedAt!: string;
    status!: string;
    message!: string;
    userFirstName!: number;
    userLastName!: number;
    startTime!: string;
    endTime!: string;
}