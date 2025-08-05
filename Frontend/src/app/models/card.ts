import { Pokemon } from './pokemon';
import { Set } from './set';

export interface Card {
    cardId: number;
    setNumber: string;
    imageURL: string;

    pokemon: Pokemon;
    cardSet: Set;
}
