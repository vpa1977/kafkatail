using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace KafkaTail.Web
{
    [Route("/api/[controller]/[action]")]
    [ApiController]
    public class TopicsController : ControllerBase
    {

        [HttpGet] public IActionResult GetTopics()
        {
            return new JsonResult(new { });
        }

        [HttpPost] public IActionResult ConfigureTopic(string topic, Dictionary<string, string> configuration)
        {
            return Ok();
        }

        [HttpPost] public IActionResult ClearTopic(string topic)
        {
            return Ok();
        }

        [HttpPost] public IActionResult CreateTopic(string topic)
        {
            return Ok();
        }

        [HttpPost] public IActionResult UploadSchema([FromForm] IFormFile file)
        {
            return Ok();
        }

        [HttpGet] public IActionResult ViewMessages(TopicFilterModel filters)
        {
            return Ok();
        }

        [HttpGet]
        public IActionResult ViewMessages(TopicFilterModel filters, string descriptor)
        {
            return Ok();
        }

    }
}
