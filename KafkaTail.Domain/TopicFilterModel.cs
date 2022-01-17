using System.Collections.Generic;

namespace KafkaTail.Domain
{
    public class TopicFilterModel
    {
        public List<string> Topics { get; set; }
        public Dictionary<string, (int, int)> Offsets { get; set; }
        public string Content { get; set; }
    }
}