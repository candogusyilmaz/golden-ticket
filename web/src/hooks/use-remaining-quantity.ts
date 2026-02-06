import { useQuery } from "@tanstack/react-query";
import { fetchClient } from "../api/open-api";

export function useRemainingQuantity(sessionId: string) {
  const { data } = useQuery({
    queryFn: async () => (await fetchClient.GET("/api/inventory")).data,
    queryKey: ["get", "/api/inventory", sessionId],
    refetchInterval: 2000,
    placeholderData: { remainingQuantity: 100 },
  });

  return data?.remainingQuantity ?? 100;
}
