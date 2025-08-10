import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PokemonSet {
  id: string;
  name: string;
  series: string;
  releaseDate: string;
  total: number;
}

export interface PokemonCard {
  id: string;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class PokemonService {
  private baseUrl = 'http://localhost:8080'; 

  constructor(private http: HttpClient) { }

  getSets(): Observable<PokemonSet[]> {
    return this.http.get<PokemonSet[]>(`${this.baseUrl}/cards/sets`);
  }


  getCardsBySet(setId: string): Observable<PokemonCard[]> {
    return this.http.get<PokemonCard[]>(`${this.baseUrl}/cards/set/${setId}`);
  }

  
}