export interface Stringing {
  stringingId: string;
  stringerUserId: string;
  ownerUserId: string;
  ownerName: string;
  racketMake: string;
  racketModel: string;
  stringType: string;
  stringColor?: string;
  mainsTensionLbs: number;
  crossesTensionLbs: number;
  state: StringingState;
  createdAt: string;
  requestedAt: string;
  canceledAt: string | null;
  declinedAt: string | null;
  receivedAt: string | null;
  inProgressAt: string | null;
  finishedAt: string | null;
  completedAt: string | null;
  failedAt: string | null;
  failedCompletedAt: string | null;
}

export interface CreateStringingRequest {
  stringerUserId: string;
  ownerUserId: string;
  racketMake: string;
  racketModel: string;
  stringType: string;
  stringColor?: string;
  mainsTensionLbs: number | null;
  crossesTensionLbs: number | null;
}

export interface UpdateStringingRequest {
  state?: StringingState;
  racketMake?: string;
  racketModel?: string;
  stringType?: string;
  stringColor?: string;
  mainsTensionLbs?: number;
  crossesTensionLbs?: number;
  stringerUserId?: string;
}

export type StringingState =
  | 'REQUESTED_BUT_NOT_DELIVERED'
  | 'CANCELED'
  | 'DECLINED'
  | 'RECEIVED_BUT_NOT_STARTED'
  | 'IN_PROGRESS'
  | 'FINISHED_BUT_NOT_PICKED_UP'
  | 'FAILED_BUT_NOT_PICKED_UP'
  | 'COMPLETED'
  | 'FAILED_COMPLETED';

export const ALL_STRINGING_STATES: StringingState[] = [
  'REQUESTED_BUT_NOT_DELIVERED',
  'CANCELED',
  'DECLINED',
  'RECEIVED_BUT_NOT_STARTED',
  'IN_PROGRESS',
  'FINISHED_BUT_NOT_PICKED_UP',
  'FAILED_BUT_NOT_PICKED_UP',
  'COMPLETED',
  'FAILED_COMPLETED'
];

export const STATE_TRANSITIONS: Record<StringingState, StringingState[]> = {
  'REQUESTED_BUT_NOT_DELIVERED': ['RECEIVED_BUT_NOT_STARTED', 'DECLINED', 'CANCELED'],
  'CANCELED': [],
  'DECLINED': [],
  'RECEIVED_BUT_NOT_STARTED': ['IN_PROGRESS'],
  'IN_PROGRESS': ['FINISHED_BUT_NOT_PICKED_UP', 'FAILED_BUT_NOT_PICKED_UP'],
  'FINISHED_BUT_NOT_PICKED_UP': ['COMPLETED'],
  'FAILED_BUT_NOT_PICKED_UP': ['FAILED_COMPLETED'],
  'COMPLETED': [],
  'FAILED_COMPLETED': []
};
